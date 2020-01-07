package com.hermant.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    public static Model load(String resourcePath, String texturesDir) throws Exception {
        return load(resourcePath, texturesDir,
                aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | aiProcess_Triangulate
                        | aiProcess_CalcTangentSpace);
    }

    public static Model load(String resourcePath, String texturesDir, int flags) throws Exception {
        long startTime = System.nanoTime();
        System.out.println("Loading: " + resourcePath);
        AIScene aiScene = aiImportFile(resourcePath, flags);
        if (aiScene == null) {
            throw new Exception("Error loading model " + resourcePath);
        }

        int numMaterials = aiScene.mNumMaterials();
        System.out.println("Number of materials: " + numMaterials);
        PointerBuffer aiMaterials = aiScene.mMaterials();
        List<Material> materials = new ArrayList<>();
        for (int i = 0; i < numMaterials; i++) {
            AIMaterial aiMaterial = AIMaterial.create(aiMaterials.get(i));
            processMaterial(aiMaterial, materials, texturesDir);
        }

        int numMeshes = aiScene.mNumMeshes();
        System.out.println("Number of meshes: " + numMeshes);
        PointerBuffer aiMeshes = aiScene.mMeshes();
        List<Mesh> meshes = new ArrayList<>();
        for (int i = 0; i < numMeshes; i++) {
            AIMesh aiMesh = AIMesh.create(aiMeshes.get(i));
            Mesh mesh = processMesh(aiMesh, materials);
            meshes.add(mesh);
        }
        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("Loaded in: " + (double)estimatedTime / 1_000_000_000.0 + " seconds");
        return new Model(meshes);
    }

    protected static void processIndices(AIMesh aiMesh, List<Integer> indices) {
        int numFaces = aiMesh.mNumFaces();
        AIFace.Buffer aiFaces = aiMesh.mFaces();
        for (int i = 0; i < numFaces; i++) {
            AIFace aiFace = aiFaces.get(i);
            IntBuffer buffer = aiFace.mIndices();
            while (buffer.remaining() > 0) {
                indices.add(buffer.get());
            }
        }
    }

    protected static void processMaterial(AIMaterial aiMaterial, List<Material> materials,
                                          String texturesDir) throws Exception {
        AIColor4D color = AIColor4D.create();
        AIString string = AIString.calloc();

        aiGetMaterialString(aiMaterial, AI_MATKEY_NAME, 0, 0, string);

        System.out.println("Processing material: " + string.dataString());

        Texture diffuseTexture = loadTexture(aiMaterial, aiTextureType_DIFFUSE, texturesDir);
        Texture specularTexture = loadTexture(aiMaterial, aiTextureType_SPECULAR, texturesDir);
        Texture ambientTexture = loadTexture(aiMaterial, aiTextureType_AMBIENT, texturesDir);
//        Texture emissiveTexture = loadTexture(aiMaterial, aiTextureType_EMISSIVE, texturesDir);
//        Texture heightTexture = loadTexture(aiMaterial, aiTextureType_HEIGHT, texturesDir);
        Texture normalsTexture = loadTexture(aiMaterial, aiTextureType_NORMALS, texturesDir);
//        Texture shininessTexture = loadTexture(aiMaterial, aiTextureType_SHININESS, texturesDir);
//        Texture opacityTexture = loadTexture(aiMaterial, aiTextureType_OPACITY, texturesDir);
//        Texture displacementTexture = loadTexture(aiMaterial, aiTextureType_DISPLACEMENT, texturesDir);
//        Texture lightMapTexture = loadTexture(aiMaterial, aiTextureType_LIGHTMAP, texturesDir);
//        Texture reflectionTexture = loadTexture(aiMaterial, aiTextureType_REFLECTION, texturesDir);
//        Texture unexpectedTexture = loadTexture(aiMaterial, aiTextureType_UNKNOWN, texturesDir);
//        Texture unexistingTexture = loadTexture(aiMaterial, aiTextureType_NONE, texturesDir);

        Color3f diffuse = loadColor(aiMaterial, AI_MATKEY_COLOR_DIFFUSE, color);
        Color3f specular = loadColor(aiMaterial, AI_MATKEY_COLOR_SPECULAR, color);
        Color3f ambient = loadColor(aiMaterial, AI_MATKEY_COLOR_AMBIENT, color);
//        Vector4f emissive = loadColor(aiMaterial, AI_MATKEY_COLOR_EMISSIVE, color);
//        Vector4f reflective = loadColor(aiMaterial, AI_MATKEY_COLOR_REFLECTIVE, color);
//        Vector4f transparent = loadColor(aiMaterial, AI_MATKEY_COLOR_TRANSPARENT, color);

        float opacity = loadFloat(aiMaterial, AI_MATKEY_OPACITY);
        float shininess = loadFloat(aiMaterial, AI_MATKEY_SHININESS);
//        float shininessStrenght = loadFloat(aiMaterial, AI_MATKEY_SHININESS_STRENGTH);
//        float refraction = loadFloat(aiMaterial, AI_MATKEY_REFRACTI);
//
//        int wireframe = loadInt(aiMaterial, AI_MATKEY_ENABLE_WIREFRAME);
//        int twosided = loadInt(aiMaterial, AI_MATKEY_TWOSIDED);
//        int shadingModel = loadInt(aiMaterial, AI_MATKEY_SHADING_MODEL);
//        int blendFunc = loadInt(aiMaterial, AI_MATKEY_BLEND_FUNC);

        Material material = new Material();

        material.setDiffuseTexture(diffuseTexture);
        material.setSpecularTexture(specularTexture);
        material.setAmbientTexture(ambientTexture);
        material.setNormalsTexture(normalsTexture);

        material.setDiffuseColour(diffuse);
        material.setSpecularColour(specular);
        material.setAmbientColour(ambient);

        material.setShininess(shininess);

        materials.add(material);
    }

    private static float loadFloat(AIMaterial aiMaterial, CharSequence type){
        float[] out = new float[1];
        int[] temp = {1};
        int loaded = aiGetMaterialFloatArray(aiMaterial, type, aiTextureType_NONE, 0, out, temp);
        if(loaded == 0) return out[0];
        return Float.NaN;
    }

    private static int loadInt(AIMaterial aiMaterial, CharSequence type){
        int[] out = new int[1];
        int[] temp = {1};
        int loaded = aiGetMaterialIntegerArray(aiMaterial, type, aiTextureType_NONE, 0, out, temp);
        if(loaded == 0) return out[0];
        return -1;
    }

    private static Color3f loadColor(AIMaterial aiMaterial, CharSequence type, AIColor4D color){
        int result = aiGetMaterialColor(aiMaterial, type, aiTextureType_NONE, 0, color);
        if(result == 0) return new Color3f(color.r(), color.g(), color.b());
        return Material.DEFAULT_COLOUR;
    }

    private static Texture loadTexture(AIMaterial aiMaterial, int type, String texturesDir) throws Exception{
        AIString string = AIString.calloc();
        Assimp.aiGetMaterialTexture(aiMaterial, type, 0, string, (IntBuffer)null, null, null, null, null, null);
        String textPath = string.dataString();
        Texture texture = null;
        if (textPath.length() > 0) {
            String textureFile = texturesDir + "/" + textPath;
            textureFile = textureFile.replace("//", "/");
            texture = TextureManager.getTexture(textureFile);
        }
        return texture;
    }

    private static Mesh processMesh(AIMesh aiMesh, List<Material> materials) {
        List<Float> positions = new ArrayList<>();
        List<Float> textures = new ArrayList<>();
        List<Float> normals = new ArrayList<>();
        List<Float> tangents = new ArrayList<>();
        List<Float> bitangents = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        System.out.println("Processing mesh: " + aiMesh.mName().dataString());

        processVertices(aiMesh, positions);
        processNormals(aiMesh, normals);
        processTextCoords(aiMesh, textures);
        processBitangents(aiMesh, bitangents);
        processTangents(aiMesh, tangents);
        processIndices(aiMesh, indices);

        ArrayList<Vertex> vertices = new ArrayList<>();
        ArrayList<Triangle> triangles = new ArrayList<>();

        for (int i = 0; i < positions.size() / 3; i++) {
            Vertex v = new Vertex();
            v.position = new Vector3f(positions.get( 3 * i), positions.get( 3 * i + 1),positions.get( 3 * i + 2));
            v.normal = new Vector3f(normals.get( 3 * i), normals.get( 3 * i + 1),normals.get( 3 * i + 2));
            v.tangent = new Vector3f(tangents.get( 3 * i), tangents.get( 3 * i + 1),tangents.get( 3 * i + 2));
            v.binormal = new Vector3f(bitangents.get( 3 * i), bitangents.get( 3 * i + 1),bitangents.get( 3 * i + 2));
            v.texture = new Vector2f(textures.get( 2 * i), textures.get( 2 * i + 1));
            vertices.add(v);
        }

        for (int i = 0; i < indices.size(); i+=3) {
            Triangle t = new Triangle(vertices.get(indices.get(i)), vertices.get(indices.get(i+1)), vertices.get(indices.get(i+2)));
            triangles.add(t);
        }

        Mesh mesh = new Mesh(triangles, vertices);
        Material material;
        int materialIdx = aiMesh.mMaterialIndex();
        if (materialIdx >= 0 && materialIdx < materials.size()) {
            material = materials.get(materialIdx);
        } else {
            material = new Material();
        }
        mesh.setMaterial(material);

        return mesh;
    }

    protected static void processBitangents(AIMesh aiMesh, List<Float> bitangents) {
        AIVector3D.Buffer aiBitangents = aiMesh.mBitangents( );
        while (aiBitangents != null && aiBitangents.remaining() > 0) {
            AIVector3D aiBitangent = aiBitangents.get();
            bitangents.add(aiBitangent.x());
            bitangents.add(aiBitangent.y());
            bitangents.add(aiBitangent.z());
        }
    }

    protected static void processTangents(AIMesh aiMesh, List<Float> tangents) {
        AIVector3D.Buffer aiTangents = aiMesh.mTangents( );
        while (aiTangents != null && aiTangents.remaining() > 0) {
            AIVector3D aiTangent = aiTangents.get();
            tangents.add(aiTangent.x());
            tangents.add(aiTangent.y());
            tangents.add(aiTangent.z());
        }
    }

    protected static void processNormals(AIMesh aiMesh, List<Float> normals) {
        AIVector3D.Buffer aiNormals = aiMesh.mNormals();
        while (aiNormals != null && aiNormals.remaining() > 0) {
            AIVector3D aiNormal = aiNormals.get();
            normals.add(aiNormal.x());
            normals.add(aiNormal.y());
            normals.add(aiNormal.z());
        }
    }

    protected static void processTextCoords(AIMesh aiMesh, List<Float> textures) {
        AIVector3D.Buffer textCoords = aiMesh.mTextureCoords(0);
        int numTextCoords = textCoords != null ? textCoords.remaining() : 0;
        for (int i = 0; i < numTextCoords; i++) {
            AIVector3D textCoord = textCoords.get();
            textures.add(textCoord.x());
            textures.add(1 - textCoord.y());
        }
    }

    protected static void processVertices(AIMesh aiMesh, List<Float> vertices) {
        AIVector3D.Buffer aiVertices = aiMesh.mVertices();
        while (aiVertices.remaining() > 0) {
            AIVector3D aiVertex = aiVertices.get();
            vertices.add(aiVertex.x());
            vertices.add(aiVertex.y());
            vertices.add(aiVertex.z());
        }
    }
}

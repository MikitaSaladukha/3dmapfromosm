package com.mygdx.game.MeshBuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ShortArray;
import com.mygdx.game.Geo;
import com.mygdx.game.Utils.CoorValidator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.topobyte.mercator.image.MercatorImage;
import com.badlogic.gdx.math.EarClippingTriangulator;

import java.util.*;

import static com.badlogic.gdx.Gdx.gl;

public class BuildingsMeshBuilder implements MeshBuilder {
    private List<Geometry> buildings = new ArrayList<>();
    private Model buildModel;

    private ModelInstance buildInstance;
    private ArrayList<ModelInstance> buildingsInst = new ArrayList<>();
    private Map<Geometry, String> levels = new HashMap<>();


    public BuildingsMeshBuilder(List<Geometry> buildings, Map<Geometry, String> levels){

        this.buildings = buildings;
        this.levels = levels;

    }

    public Model buildMesh(MercatorImage mercatorImage){

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("polys", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal
                        | VertexAttributes.Usage.TextureCoordinates, new Material());

        float prCoorX = 0.0f;
        float prCoorY = 0.0f;
        float thisCoorX = 0.0f;
        float thisCoorY = 0.0f;
        float prprCoorX = 0.0f;
        float prprCoorY = 0.0f;
        float height = 0.0f;
        float[] vertForTr = new float[0];
        EarClippingTriangulator tr = new EarClippingTriangulator();
        ShortArray triangled;
        int i = 0;

        Random rand = new Random();
        for (Geometry build : buildings){
            try {
                try {
                    height = Float.parseFloat(levels.get(build)) * 10;
                }catch (NumberFormatException e){height = 20.0f;}
            }catch (NullPointerException ex){
                height = 20.0f;
            }

            builder.setColor(new Color((float) (rand.nextFloat() / 2f + 0.2),
                    (float) (rand.nextFloat() / 2f + 0.2),
                    (float) (rand.nextFloat() / 2f + 0.2), 0.2f ));
            //System.out.println(build.getNumPoints() + " nP" + build.getCoordinates().length + " crLen");
            vertForTr = new float[build.getNumPoints()*2];

            for (Coordinate cor : build.getCoordinates()){
                if (CoorValidator.validateCoor(mercatorImage.getDefiningBoundingBox(), cor)) {
                //if (CoorValidator.validateCoor(Geo.getCamPos(), cor)) {
                    if (i == 0) {
                        prCoorX = (float) mercatorImage.getX(cor.x);
                        prCoorY = (float) mercatorImage.getY(cor.y);
                        vertForTr[i] = prCoorX;
                        vertForTr[i+1] = prCoorY;
                        i++;
                        continue;
                    }
                    thisCoorX = (float) mercatorImage.getX(cor.x);
                    thisCoorY = (float) mercatorImage.getY(cor.y);

                    vertForTr[i * 2 ] = thisCoorX;
                    vertForTr[i * 2 + 1] = thisCoorY;


                    gl.glEnable(GL20.GL_CULL_FACE);
                    gl.glCullFace(GL20.GL_FRONT_AND_BACK);
                    gl.glFrontFace(GL20.GL_CW);

                    builder.triangle(new Vector3(prCoorX, 0.0f, prCoorY),
                            new Vector3(thisCoorX, 0.0f, thisCoorY),
                            new Vector3(prCoorX, height, prCoorY));
                    builder.triangle(new Vector3(prCoorX, height, prCoorY),
                            new Vector3(thisCoorX, height, thisCoorY),
                            new Vector3(thisCoorX, 0.0f, thisCoorY));

                    builder.triangle(new Vector3(prCoorX, height, prCoorY),
                            new Vector3(thisCoorX, 0.0f, thisCoorY),
                            new Vector3(prCoorX, 0.0f, prCoorY));
                    builder.triangle(new Vector3(thisCoorX, 0.0f, thisCoorY),
                            new Vector3(thisCoorX, height, thisCoorY),
                            new Vector3(prCoorX, height, prCoorY));


                    prprCoorX = prCoorX;
                    prprCoorY = prCoorY;

                    prCoorX = thisCoorX;
                    prCoorY = thisCoorY;

                    builder.triangle(new Vector3(prprCoorX, height, prprCoorY),
                            new Vector3(prCoorX, height, prCoorY),
                            new Vector3(thisCoorX, height, thisCoorY));
                    //System.out.println(cor + " b cor");
                    //System.out.println(Arrays.toString(verticesForTriangulation) + "vert ");
                    i++;
                }
            }
            //System.out.println(tr.computeTriangles(verticesForTriangulation));
            //System.out.println(build + "build ");
            gl.glFrontFace(GL20.GL_CCW);
            triangled = tr.computeTriangles(vertForTr);
            for (int j = 0; j < triangled.size; j += 3) {
                int p1 = triangled.get(j) * 2;
                int p2 = triangled.get(j + 1) * 2;
                int p3 = triangled.get(j + 2) * 2;
                if (vertForTr[p1] == 0 || vertForTr[p1 + 1] == 0)
                    continue;
                if (vertForTr[p2] == 0 || vertForTr[p2 + 1] == 0)
                    continue;
                if (vertForTr[p3] == 0 || vertForTr[p3 + 1] == 0)
                    continue;
                try {
                    builder.triangle(new Vector3(vertForTr[p1], height, vertForTr[p1 + 1]),
                            new Vector3(vertForTr[p2], height, vertForTr[p2 + 1]),
                            new Vector3(vertForTr[p3], height, vertForTr[p3 + 1]));

                }catch (GdxRuntimeException ex){
                    continue;
                }
            }
            i = 0;
        }

        //gl.glFrontFace(GL20.GL_CW);
        /*
        builder.setColor(Color.GRAY);
        //gl.glFrontFace(GL20.GL_CCW);
        builder.triangle(new Vector3((float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon2()),
                        0.0f, (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat2())),

                new Vector3((float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon2()), 0.0f,
                        (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat1())),

                new Vector3((float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon1()),
                        0.0f, (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat1())));

        builder.triangle(new Vector3((float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon1()),
                        0.0f, (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat1())),

                new Vector3((float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon1()), 0.0f,
                        (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat2())),

                new Vector3((float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon2()),
                        0.0f, (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat2())));

*/
        buildModel = modelBuilder.end();
        return buildModel;
    }
}

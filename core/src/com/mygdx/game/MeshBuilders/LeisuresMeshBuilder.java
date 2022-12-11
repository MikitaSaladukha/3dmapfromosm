package com.mygdx.game.MeshBuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.DelaunayTriangulator;
import com.badlogic.gdx.math.EarClippingTriangulator;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ShortArray;
import com.mygdx.game.Utils.CoorValidator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import de.topobyte.mercator.image.MercatorImage;

import java.util.ArrayList;
import java.util.List;

import static com.badlogic.gdx.Gdx.gl;

public class LeisuresMeshBuilder implements MeshBuilder{
    private List<Geometry> leisures = new ArrayList<>();
    private Model leisureModel;
    private ModelInstance leisureInstance;

    public LeisuresMeshBuilder(List<Geometry> leisures){
        this.leisures = leisures;
    }

    @Override
    public Model buildMesh(MercatorImage mercatorImage) {
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("leisures", GL20.GL_TRIANGLES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.FOREST); //Forest


        float prCoorX = 0.0f;
        float prCoorY = 0.0f;
        float thisCoorX = 0.0f;
        float thisCoorY = 0.0f;
        float[] vertForTr = new float[0];
        //EarClippingTriangulator tr = new EarClippingTriangulator();
        DelaunayTriangulator tr = new DelaunayTriangulator();
        ShortArray triangled;

        int i = 0;

        for (Geometry leisure : leisures){
            try {
                vertForTr = new float[leisure.getNumPoints()*2];
                for (Coordinate coordinate: leisure.getCoordinates()) {
                    if (CoorValidator.validateCoor(mercatorImage.getDefiningBoundingBox(), coordinate)) {
                        if (i == 0) {
                            prCoorX = (float) mercatorImage.getX(coordinate.x);
                            prCoorY = (float) mercatorImage.getY(coordinate.y);
                            vertForTr[i] = prCoorX;
                            vertForTr[i+1] = prCoorY;
                            i++;
                            continue;
                        }
                        thisCoorX = (float) mercatorImage.getX(coordinate.x);
                        thisCoorY = (float) mercatorImage.getY(coordinate.y);

                        vertForTr[i*2] = thisCoorX;
                        vertForTr[i*2+1] = thisCoorY;

                       // builder.line(prCoorX, 0.2f, prCoorY, thisCoorX, 0.2f, thisCoorY);

                        prCoorX = (float) mercatorImage.getX(coordinate.x);
                        prCoorY = (float) mercatorImage.getY(coordinate.y);
                        i++;
                    }
                }
            } catch (NullPointerException e){
                ;
            }

            gl.glFrontFace(GL20.GL_CCW);
            triangled = tr.computeTriangles(vertForTr, false);
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
                    builder.triangle(new Vector3(vertForTr[p1], 0.0f, vertForTr[p1 + 1]),
                            new Vector3(vertForTr[p2], 0.0f, vertForTr[p2 + 1]),
                            new Vector3(vertForTr[p3], 0.0f, vertForTr[p3 + 1]));

                }catch (GdxRuntimeException ex){
                    continue;
                }
            }


            i = 0;

        }

        leisureModel = modelBuilder.end();
        //leisureInstance = new ModelInstance(leisureModel);

        return leisureModel;
    }
}

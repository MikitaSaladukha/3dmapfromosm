package com.mygdx.game.MeshBuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.CoorValidator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import de.topobyte.mercator.image.MercatorImage;

import java.util.ArrayList;
import java.util.List;

public class RoadsMeshBuilder implements MeshBuilder{

    private List<LineString> streets = new ArrayList<>();
    private Model streetModel;
    private ModelInstance streetInstance;

    public RoadsMeshBuilder(List<LineString> streets){

        this.streets = streets;
    }

    public Model buildMesh(MercatorImage mercatorImage){
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();
        SphereShapeBuilder shapeBuilder = new SphereShapeBuilder();
        MeshPartBuilder builder = modelBuilder.part("paths", GL20.GL_LINES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorPacked | VertexAttributes.Usage.TextureCoordinates, new Material());
        builder.setColor(Color.YELLOW);


        float prCoorX = 0.0f;
        float prCoorY = 0.0f;
        float prCoorX1 = 0.0f;
        float prCoorY1 = 0.0f;
        float thisCoorX = 0.0f;
        float thisCoorY = 0.0f;
        float thisCoorX1 = 0.0f;
        float thisCoorY1 = 0.0f;
        float width;
        float height;
        int i = 0;
        for (LineString street : streets){
            try {
                for (Coordinate coordinate: street.getCoordinates()) {
                    //System.out.println(coordinate.x +" "+ coordinate.y + " coor");
                    if (CoorValidator.validateCoor(mercatorImage.getDefiningBoundingBox(), coordinate)) {

                        if (i == 0) {

                            prCoorX = (float) mercatorImage.getX(coordinate.x);
                            prCoorY = (float) mercatorImage.getY(coordinate.y);
                            i++;
                            continue;
                        }
                        thisCoorX = (float) mercatorImage.getX(coordinate.x);
                        thisCoorY = (float) mercatorImage.getY(coordinate.y);
/*
                        builder.triangle(new Vector3(prCoorX - 10.0f, 0.1f, prCoorY),
                                new Vector3(prCoorX + 10.0f, 0.1f, prCoorY),
                                new Vector3(thisCoorX + 10.0f, 0.1f, thisCoorY));

                        builder.triangle(new Vector3(thisCoorX + 10.0f, 0.1f, thisCoorY),
                                new Vector3(thisCoorX - 10.0f, 0.1f, thisCoorY),
                                new Vector3(prCoorX - 10.0f, 0.1f, prCoorY));
*/
                        builder.line(prCoorX, 0.1f, prCoorY, thisCoorX, 0.1f, thisCoorY);
                        prCoorX = (float) mercatorImage.getX(coordinate.x);
                        prCoorY = (float) mercatorImage.getY(coordinate.y);
                    }
                    //builder.rect((short) 1, (short) 1, (short) 2, (short) 2);
                }
            } catch (NullPointerException e){
                ;
            }
            i = 0;
            //System.out.println(names.get(street) + " nam");


        }

        streetModel = modelBuilder.end();
        //streetInstance = new ModelInstance(streetModel);
        return streetModel;
    }
}

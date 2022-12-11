package com.mygdx.game.MeshBuilders;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.mygdx.game.Utils.CoorValidator;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import de.topobyte.mercator.image.MercatorImage;

import java.util.ArrayList;
import java.util.List;

public class RailwaysMeshBuilder implements MeshBuilder{

    private List<LineString> rails = new ArrayList<>();
    private Model railwayModel;
    private ModelInstance railwayInstance;


    public RailwaysMeshBuilder(List<LineString> rails){

        this.rails = rails;

    }
    @Override
    public Model buildMesh(MercatorImage mercatorImage) {
        ModelBuilder modelBuilder = new ModelBuilder();

        modelBuilder.begin();
        MeshPartBuilder builder = modelBuilder.part("rails", GL20.GL_LINES,
                VertexAttributes.Usage.Position | VertexAttributes.Usage.ColorUnpacked, new Material());
        builder.setColor(Color.RED);


        float prCoorX = 0.0f;
        float prCoorY = 0.0f;
        float thisCoorX = 0.0f;
        float thisCoorY = 0.0f;

        int i = 0;

        for (LineString rail : rails){
            try {
                for (Coordinate coordinate: rail.getCoordinates()) {

                    if (CoorValidator.validateCoor(mercatorImage.getDefiningBoundingBox(), coordinate)) {
                        if (i == 0) {
                            prCoorX = (float) mercatorImage.getX(coordinate.x);
                            prCoorY = (float) mercatorImage.getY(coordinate.y);
                            i++;
                            continue;
                        }
                        thisCoorX = (float) mercatorImage.getX(coordinate.x);
                        thisCoorY = (float) mercatorImage.getY(coordinate.y);

                        builder.line(prCoorX, 0.2f, prCoorY, thisCoorX, 0.2f, thisCoorY);

                        prCoorX = (float) mercatorImage.getX(coordinate.x);
                        prCoorY = (float) mercatorImage.getY(coordinate.y);
                    }

                }
            } catch (NullPointerException e){
                ;
            }
            i = 0;

        }

        railwayModel = modelBuilder.end();
        //railwayInstance = new ModelInstance(railwayModel);

        return railwayModel;
    }

}

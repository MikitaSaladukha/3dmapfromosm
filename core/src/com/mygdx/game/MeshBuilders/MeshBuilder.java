package com.mygdx.game.MeshBuilders;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import de.topobyte.mercator.image.MercatorImage;

public interface MeshBuilder {

    public Model buildMesh(MercatorImage mercatorImage);
}

package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.SphereShapeBuilder;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.MeshBuilders.*;
import com.vividsolutions.jts.geom.*;
import de.topobyte.adt.geo.BBox;
import de.topobyte.jgs.transform.CoordinateTransformer;
import de.topobyte.jts2awt.Jts2Awt;
import de.topobyte.mercator.image.MercatorImage;
import de.topobyte.osm4j.core.access.DefaultOsmHandler;
import de.topobyte.osm4j.core.access.OsmInputException;
import de.topobyte.osm4j.core.access.OsmIteratorInput;
import de.topobyte.osm4j.core.access.OsmReader;
import de.topobyte.osm4j.core.dataset.InMemoryMapDataSet;
import de.topobyte.osm4j.core.dataset.MapDataSetLoader;
import de.topobyte.osm4j.core.model.iface.*;
import de.topobyte.osm4j.core.model.util.OsmModelUtil;
import de.topobyte.osm4j.core.resolve.EntityFinder;
import de.topobyte.osm4j.core.resolve.EntityFinders;
import de.topobyte.osm4j.core.resolve.EntityNotFoundException;
import de.topobyte.osm4j.core.resolve.EntityNotFoundStrategy;
import de.topobyte.osm4j.geometry.*;
import de.topobyte.osm4j.utils.FileFormat;
import de.topobyte.osm4j.utils.OsmFile;
import de.topobyte.osm4j.utils.OsmFileInput;
import de.topobyte.osm4j.utils.OsmSingleIteratorInput;
import de.topobyte.osm4j.xml.dynsax.OsmXmlIterator;
import de.topobyte.osm4j.xml.dynsax.OsmXmlReader;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Geo {

    private  BBox bbox;
    private MercatorImage mercatorImage;
    private int width;
    private int height;
    private InMemoryMapDataSet data;
    private Set<String> validHighways = new HashSet<>(
            Arrays.asList("primary", "secondary", "tertiary",
                    "residential", "living_street", "trunk", "pedestrian", "footway",
                    "steps", "motorway", "service", "cycleway", "path"));

    private Set<String> validLeisures = new HashSet<>(
            Arrays.asList("park", "pitch", "garden"));
    private List<LineString> streets = new ArrayList<>();

    private List<Geometry> buildings = new ArrayList<>();
    private List<Geometry> waterways = new ArrayList<>();

    private List<Geometry> leisures = new ArrayList<>();
    private Map<Geometry, String> levels = new HashMap<>();
    private List<LineString> railways = new ArrayList<>();

    private String currentStreet;
    private Map<LineString, String> names = new HashMap<>();
    private WayBuilder wayBuilder;
    private RegionBuilder regionBuilder;

    static Vector3 pos;



    public Geo(int width, int height) throws IOException, OsmInputException {
        // This is the region we would like to render
        BBox bbox = new BBox(27.4692,53.8658,27.4863,53.8720);
        mercatorImage = new MercatorImage(bbox, 1920, 1080);

        this.bbox = bbox;
        this.height = height;
        this.width = width;
        wayBuilder = new WayBuilder();
        regionBuilder = new RegionBuilder();

        String queryTemplate = "http://overpass-api.de/api/interpreter?data=(node(%f,%f,%f,%f);<;>;);out;";
        String query = String.format(queryTemplate, bbox.getLat2(),
                bbox.getLon1(), bbox.getLat1(), bbox.getLon2());


        InputStream input = new URL(query).openStream();
        InMemoryMapDataSet dataSet = new InMemoryMapDataSet();

        //InputStream input2 = Files.newInputStream(new File("C:\\Users\\ilyas\\Downloads\\map").toPath());
        OsmReader reader = new OsmXmlReader(input, false);
        data = MapDataSetLoader.read(reader, true, true,
                true);
    }

    public boolean refreshMercatorImage() throws IOException, OsmInputException {
        double nX = mercatorImage.getLon1();
        double nY = mercatorImage.getLat1();
        double X = mercatorImage.getLon2();
        double Y = mercatorImage.getLat2();
        boolean refresh = false;
        BBox nBbox;
        System.out.println("refr!");
        if (pos.x <= (float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon1())) {
            nX = mercatorImage.getLon1() - mercatorImage.getImageSx();
            X = mercatorImage.getLon1();
            refresh = true;
        }
        if (pos.x >= (float) mercatorImage.getX(mercatorImage.getDefiningBoundingBox().getLon2())) {
            nX = mercatorImage.getLon2() + mercatorImage.getImageSx();
            X = mercatorImage.getLon2();
            refresh = true;
        }
        if (pos.z <= (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat1())){
            nY = mercatorImage.getLat1() - mercatorImage.getImageSy();
            Y = mercatorImage.getLat1();
            refresh = true;
        }
        if (pos.z >= (float) mercatorImage.getY(mercatorImage.getDefiningBoundingBox().getLat2())){
            nY = mercatorImage.getLat2() + mercatorImage.getImageSy();
            Y = mercatorImage.getLat2();
            refresh = true;
        }
        if (refresh){
            System.out.println("REfresh!");
            nBbox = new BBox(nX, nY, X, Y);
            this.mercatorImage = new MercatorImage(nBbox, getWidth(), getHeight());
            String queryTemplate = "http://overpass-api.de/api/interpreter?data=(node(%f,%f,%f,%f);<;>;);out;";
            String query = String.format(queryTemplate, bbox.getLat2(),
                    bbox.getLon1(), bbox.getLat1(), bbox.getLon2());
            InputStream input = new URL(query).openStream();
            InMemoryMapDataSet dataSet = new InMemoryMapDataSet();
            OsmReader reader = new OsmXmlReader(input, false);
            this.data = MapDataSetLoader.read(reader, true, true,
                    true);
        }
        return refresh;
    }

    public MercatorImage getMercatorImage(){ return  mercatorImage; }

    public int getWidth(){
        return  width;
    }
    public int getHeight(){
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setBbox(BBox bbox) {
        this.bbox = bbox;
    }

    public void collectData(){

        Set<OsmWay>buildingRelationWays = new HashSet<>();
        Set<OsmWay> leisuresRelationWays = new HashSet<>();
        Set<OsmWay> waterRelationWays = new HashSet<>();
        EntityFinder wayFinder = EntityFinders.create(data, EntityNotFoundStrategy.IGNORE);
        //find streets
        for (OsmWay way: data.getWays().valueCollection()){

            Map<String,String> tags = OsmModelUtil.getTagsAsMap(way);
            String higway = tags.get("highway");
            //System.out.println(tags);
            if (higway == null)
                continue;

            Collection<LineString> paths = getLine(way);
            if (!validHighways.contains(higway))
                continue;
            for (LineString path : paths){
                streets.add(path);

            }

            String name = tags.get("name");
            if (name == null)
                continue;
            for (LineString path : paths){
                names.put(path, name);
                //System.out.println(path + " " + name);
            }


        }
        for (OsmWay way : data.getWays().valueCollection()){
            Map<String,String> tags = OsmModelUtil.getTagsAsMap(way);
            String railw = tags.get("railway");
            //System.out.println(railw + " rlw");
            Collection<LineString> paths = getLine(way);
            if (railw == null)
                continue;
            for (LineString path: paths){
                railways.add(path);
            }
        }
        //find buildings
        for (OsmRelation relation : data.getRelations().valueCollection()){

            Map<String, String> tags = OsmModelUtil.getTagsAsMap(relation);
            //System.out.println(tags);
            if (tags.containsKey("building")){
                MultiPolygon area = getPolygon(relation);
                if (area != null){
                    buildings.add(area);
                    levels.put(area, tags.get("building:levels"));
                    //levels.put(area, tags.get("height"));
                    //System.out.println(area.toString());
                    /*for (Coordinate coordinate: area.getCoordinates()){
                        System.out.println(coordinate.x + " cor " + coordinate.y + " " + coordinate.z);
                    }
                     */
                }
                try {
                    wayFinder.findMemberWays(relation, buildingRelationWays);
                }catch (EntityNotFoundException ex){

                }
            }
        }

        //find buildings from ways
        for (OsmWay way : data.getWays().valueCollection()){

            if (buildingRelationWays.contains(way)){
                continue;
            }
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);

            if (tags.containsKey("building")){
                MultiPolygon area = getPolygon(way);
                if (area != null){
                    buildings.add(area);
                    levels.put(area, tags.get("building:levels"));
                    /*for (Coordinate coordinate: area.getCoordinates()){
                        System.out.println(coordinate.x + " cor " + coordinate.y + " " + coordinate.z);
                    }
                     */
                }
            }
        }

        for (OsmRelation relation : data.getRelations().valueCollection()){
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(relation);

            if (tags.containsValue("residential"))
                continue;

            if (tags.containsKey("water")|| tags.containsKey("waterway")){
                MultiPolygon area = getPolygon(relation);
                if (area != null)
                    waterways.add(area);
                System.out.println(waterways.size() + " wsize");
            }
            if (tags.containsKey("leisure") || tags.containsKey("landuse")||
            tags.containsValue("wood")){
                MultiPolygon area2 = getPolygon(relation);
                if (area2 != null)
                    leisures.add(area2);
                System.out.println(leisures.size() + " lsize");
            }

            try {
                wayFinder.findMemberWays(relation, leisuresRelationWays);
                wayFinder.findMemberWays(relation, waterRelationWays);
            }catch (EntityNotFoundException ex){

            }
        }

        for (OsmWay way : data.getWays().valueCollection()){

            if (leisuresRelationWays.contains(way)){
                continue;
            }
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);
            if (tags.containsValue("residential"))
                continue;
            if (tags.containsKey("leisure") || tags.containsKey("landuse") ||
                    tags.containsValue("wood")){
                MultiPolygon area = getPolygon(way);
                if (area != null){
                    leisures.add(area);
                }
            }
        }

        for (OsmWay way : data.getWays().valueCollection()){

            if (waterRelationWays.contains(way)){
                continue;
            }
            Map<String, String> tags = OsmModelUtil.getTagsAsMap(way);

            if (tags.containsKey("waterway") || tags.containsKey("water")){
                MultiPolygon area = getPolygon(way);
                if (area != null){
                    waterways.add(area);
                }
            }
        }
    }

    private Collection<LineString> getLine(OsmWay way){
        List<LineString> results = new ArrayList<>();
        try {
            WayBuilderResult lines = wayBuilder.build(way, data);
            results.addAll(lines.getLineStrings());
            if (lines.getLineStrings() != null){
                results.add(lines.getLinearRing());
            }
        }catch (EntityNotFoundException e){

        }
        return results;
    }

    private MultiPolygon getPolygon(OsmWay way){
        try {
            RegionBuilderResult region = regionBuilder.build(way,data);
            return region.getMultiPolygon();
        } catch (EntityNotFoundException e) {
            return null;
        }
    }

    private MultiPolygon getPolygon(OsmRelation relation){
        try {
            RegionBuilderResult region = regionBuilder.build(relation, data);
            return region.getMultiPolygon();
        }catch (EntityNotFoundException exception){
            return null;
        }
    }

    public String getStreetName(){
        float thisCoorX = 0.0f;
        float thisCoorY = 0.0f;
        float prCoorX = 0.0f;
        float prCoorY = 0.0f;
        int i = 0;
        for (LineString street : streets){
            try {
                for (Coordinate cor : street.getCoordinates()) {
                    if (i == 0) {
                        prCoorX = (float) mercatorImage.getX(cor.x);
                        prCoorY = (float) mercatorImage.getY(cor.y);
                        i++;
                        continue;
                    }
                    thisCoorX = (float) mercatorImage.getX(cor.x);
                    thisCoorY = (float) mercatorImage.getY(cor.y);

                    if (((pos.x >= prCoorX - 10 & pos.x <= thisCoorX + 10)
                            &((pos.y >= prCoorY - 10 & pos.y <= thisCoorY + 10)
                            ||(pos.y <= prCoorY + 10 & pos.y >= thisCoorY - 10)))
                            || ((pos.x <= prCoorX + 10 & pos.x >= thisCoorX - 10)
                            &((pos.y >= prCoorY - 10 & pos.y <= thisCoorY + 10)
                            ||(pos.y <= prCoorY + 10 & pos.y >= thisCoorY - 10)))) {
                        currentStreet = names.get(street);
                    }
                    prCoorX = (float) mercatorImage.getX(cor.x);
                    prCoorY = (float) mercatorImage.getY(cor.y);
                }
            }catch (NullPointerException exception){

            }
            i = 0;
        }
        return currentStreet;
    }

    public void setCamPos(Vector3 pos){
        this.pos = pos;
    }

    public static Vector3 getCamPos(){
        return pos;
    }

    public Model buildBuilding(){
        BuildingsMeshBuilder bMB = new BuildingsMeshBuilder(buildings, levels);
        return bMB.buildMesh(mercatorImage);
    }

    public Model buildStreets(){
        RoadsMeshBuilder rMB = new RoadsMeshBuilder(streets);
        return rMB.buildMesh(mercatorImage);
    }

    public Model buildRails(){
        RailwaysMeshBuilder rlMB = new RailwaysMeshBuilder(railways);
        return rlMB.buildMesh(mercatorImage);
    }

    public Model buildWater(){
        WaterwaysMeshBuilder wMB = new WaterwaysMeshBuilder(waterways);
        return wMB.buildMesh(mercatorImage);
    }

    public Model buildLeisures(){
        LeisuresMeshBuilder lMb = new LeisuresMeshBuilder(leisures);
        return lMb.buildMesh(mercatorImage);
    }
}

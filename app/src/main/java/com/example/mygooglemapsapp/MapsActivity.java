package com.example.mygooglemapsapp;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * This shows to include a map in lite mode in a ListView.
 * Note the use of the view holder pattern with the
 * {@link com.google.android.gms.maps.OnMapReadyCallback}.
 */
public class MapsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mGridLayoutManager = new GridLayoutManager(this, 2);
        mLinearLayoutManager = new LinearLayoutManager(this);

        // Set up the RecyclerView
        mRecyclerView = findViewById(R.id.recycler_view);           //Busquem la RecyclerView a la layout
        mRecyclerView.setHasFixedSize(true);                        //Deixem a true per no canviar el tamany de la layout
        mRecyclerView.setLayoutManager(mLinearLayoutManager);       //Utilitzem LinearLayoutManager com a Administrador de diseny (Per defecte)
        mRecyclerView.setAdapter(new MapAdapter(LIST_LOCATIONS));   //Afegim la LIST_LOCATIONS a l'Adapter
        mRecyclerView.setRecyclerListener(mRecycleListener);        //Afegim el Listener
    }

    /** Create a menu to switch between Linear and Grid LayoutManager. */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lite_list_menu, menu);     //Inflem el Menú
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.layout_linear:
                mRecyclerView.setLayoutManager(mLinearLayoutManager);       //En cas d'escollir la LINEAR layout al Menú
                break;
            case R.id.layout_grid:
                mRecyclerView.setLayoutManager(mGridLayoutManager);         //En cas d'escollir la GRID layout al Menú
                break;
        }
        return true;
    }

    /**
     * Adapter that displays a title and {@link com.google.android.gms.maps.MapView} for each item.
     * The layout is defined in <code>lite_list_demo_row.xml</code>. It contains a MapView
     * that is programatically initialised in
     * {@link #(int, android.view.View, android.view.ViewGroup)}
     */
    private class MapAdapter extends RecyclerView.Adapter<MapAdapter.ViewHolder> {

        private NamedLocation[] namedLocations;

        private MapAdapter(NamedLocation[] locations) {
            super();
            namedLocations = locations;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.activity_maps_row, parent, false));   //Infla l'Activity maps_row que conte el nom del lloc i la localització
        }

        /**
         * This function is called when the user scrolls through the screen and a new item needs
         * to be shown. So we will need to bind the holder with the details of the next item.
         */
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {     //El que fa aquest metode es anar reciclant les posicions de la recyclerView
            if (holder == null) {                                           //amb les noves dades
                return;
            }
            holder.bindView(position);                  //Crida a bindView amb la posicio on aniran les noves dades
        }

        @Override
        public int getItemCount() {
            return namedLocations.length;           //Ens retorna el numero total d'elements en del conjunt
        }

        /**
         * Holder for Views used in the {@link MapsActivity.MapAdapter}.
         * Once the  the <code>map</code> field is set, otherwise it is null.
         * When the {@link #onMapReady(com.google.android.gms.maps.GoogleMap)} callback is received and
         * the {@link com.google.android.gms.maps.GoogleMap} is ready, it stored in the {@link #map}
         * field. The map is then initialised with the NamedLocation that is stored as the tag of the
         * MapView. This ensures that the map is initialised with the latest data that it should
         * display.
         */
        class ViewHolder extends RecyclerView.ViewHolder implements OnMapReadyCallback {

            MapView mapView;
            TextView title;
            GoogleMap map;
            View layout;

            private ViewHolder(View itemView) {
                super(itemView);
                layout = itemView;
                mapView = layout.findViewById(R.id.lite_listrow_map);               //Agafem referencies de l'activity maps_row
                title = layout.findViewById(R.id.lite_listrow_text);
                if (mapView != null) {
                    // Initialise the MapView
                    mapView.onCreate(null);
                    // Set the map ready callback to receive the GoogleMap object
                    mapView.getMapAsync(this);
                }
            }

            @Override
            public void onMapReady(GoogleMap googleMap) {
                MapsInitializer.initialize(getApplicationContext());        //Inicialitzem el mapa que es mostrara en la miniatura
                map = googleMap;
                setMapLocation();
            }

            /**
             * Displays a {@link MapsActivity.NamedLocation} on a
             * {@link com.google.android.gms.maps.GoogleMap}.
             * Adds a marker and centers the camera on the NamedLocation with the normal map type.
             */
            private void setMapLocation() {
                if (map == null) return;

                NamedLocation data = (NamedLocation) mapView.getTag();
                if (data == null) return;

                // Add a marker for this item and set the camera
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(data.location, 14.5f));        //Agafem la Longitud i la latitud i el zoom
                map.addMarker(new MarkerOptions().position(data.location));                        //Afegim un marcador en la posició

                // Set the map type back to normal.
                map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }

            private void bindView(int pos) {
                NamedLocation item = namedLocations[pos];
                // Store a reference of the ViewHolder object in the layout.
                layout.setTag(this);
                // Store a reference to the item in the mapView's tag. We use it to get the
                // coordinate of a location, when setting the map location.
                mapView.setTag(item);
                setMapLocation();
                title.setText(item.name);
            }
        }
    }

    /**
     * RecycleListener that completely clears the {@link com.google.android.gms.maps.GoogleMap}
     * attached to a row in the RecyclerView.
     * Sets the map type to {@link com.google.android.gms.maps.GoogleMap#MAP_TYPE_NONE} and clears
     * the map.
     */
    private RecyclerView.RecyclerListener mRecycleListener = new RecyclerView.RecyclerListener() {

        @Override
        public void onViewRecycled(RecyclerView.ViewHolder holder) {
            MapAdapter.ViewHolder mapHolder = (MapAdapter.ViewHolder) holder;
            if (mapHolder != null && mapHolder.map != null) {
                // Clear the map and free up resources by changing the map type to none.
                // Also reset the map when it gets reattached to layout, so the previous map would
                // not be displayed.
                mapHolder.map.clear();
                mapHolder.map.setMapType(GoogleMap.MAP_TYPE_NONE);
            }
        }
    };

    /**
     * Location represented by a position ({@link com.google.android.gms.maps.model.LatLng} and a
     * name ({@link java.lang.String}).
     */
    private static class NamedLocation {

        public final String name;
        public final LatLng location;

        NamedLocation(String name, LatLng location) {
            this.name = name;
            this.location = location;
        }
    }

    /**
     * A list of locations to show in this ListView.
     */
    private static final NamedLocation[] LIST_LOCATIONS = new NamedLocation[]{
            new NamedLocation("UdL", new LatLng(41.608287, 0.623425)),

            new NamedLocation("Mercadona", new LatLng(41.611681, 0.628164)),
            new NamedLocation("Bon Preu", new LatLng(41.610767, 0.621612)),
            new NamedLocation("Carrefour", new LatLng(41.617214, 0.613106)),

            new NamedLocation("Nuba African Tavern", new LatLng(41.609107, 0.633533)),
            new NamedLocation("JCA Cinemes Lleida-Alpicat", new LatLng(41.650150, 0.567857)),

            new NamedLocation("Clínica HLA Perpetuo Socorro", new LatLng(41.614352, 0.618771)),
            new NamedLocation("Hospital Universitari Arnau de Vilanova", new LatLng(41.626887, 0.613470))
    };

}


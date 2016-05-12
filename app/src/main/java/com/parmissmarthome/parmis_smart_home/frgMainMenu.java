package com.parmissmarthome.parmis_smart_home;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
//import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.GridView;

import com.parmissmarthome.parmis_smart_home.db.mainmenudb;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link frgMainMenu.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link frgMainMenu#newInstance} factory method to
 * create an instance of this fragment.
 */
public class frgMainMenu extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Context context;

    private GridView photoGrid;
    private int mPhotoSize, mPhotoSpacing;
    private pjadpmainmenu adp;

    /*private ImageAdapter imageAdapter;
    private static final String[] CONTENT = new String[] { "Akon", "Justin Bieber", "AlRight", "Big Sean",
            "Britney Spears", "Hilary", "Micheal Buble", "Akon", "Justin Bieber", "AlRight", "Big Sean",
            "Britney Spears", "Hilary", "Micheal Buble", "Britney Spears", "Hilary", "Micheal Buble", "Akon",
            "Justin Bieber", "AlRight", "Big Sean", "Britney Spears", "Hilary", "Micheal Buble", "Akon",
            "Justin Bieber", "AlRight", "Big Sean", "Britney Spears", "Hilary", "Micheal Buble", "Akon",
            "Justin Bieber", "AlRight", "Big Sean", "Britney Spears", "Hilary", "Micheal Buble", "Britney Spears",
            "Hilary", "Micheal Buble", "Akon", "Justin Bieber", "AlRight", "Big Sean", "Britney Spears", "Hilary",
            "Micheal Buble" };
    private static final int[] ICONS = new int[] { R.drawable.cover_akon, R.drawable.cover_justin,
            R.drawable.cover_alright, R.drawable.cover_big_sean, R.drawable.cover_britney, R.drawable.cover_hilary,
            R.drawable.cover_mb, R.drawable.cover_akon, R.drawable.cover_justin, R.drawable.cover_alright,
            R.drawable.cover_big_sean, R.drawable.cover_britney, R.drawable.cover_hilary, R.drawable.cover_mb,
            R.drawable.cover_britney, R.drawable.cover_hilary, R.drawable.cover_mb, R.drawable.cover_akon,
            R.drawable.cover_justin, R.drawable.cover_alright, R.drawable.cover_big_sean, R.drawable.cover_britney,
            R.drawable.cover_hilary, R.drawable.cover_mb, R.drawable.cover_akon, R.drawable.cover_justin,
            R.drawable.cover_alright, R.drawable.cover_big_sean, R.drawable.cover_britney, R.drawable.cover_hilary,
            R.drawable.cover_mb, R.drawable.cover_akon, R.drawable.cover_justin, R.drawable.cover_alright,
            R.drawable.cover_big_sean, R.drawable.cover_britney, R.drawable.cover_hilary, R.drawable.cover_mb,
            R.drawable.cover_britney, R.drawable.cover_hilary, R.drawable.cover_mb, R.drawable.cover_akon,
            R.drawable.cover_justin, R.drawable.cover_alright, R.drawable.cover_big_sean, R.drawable.cover_britney,
            R.drawable.cover_hilary, R.drawable.cover_mb };*/

    // TODO: Rename and change types of parameters
//    private String mParam1;
//    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Cursor maincursor;

    // TODO: Rename and change types and number of parameters
    public static frgMainMenu newInstance() {
        frgMainMenu fragment = new frgMainMenu();
        return fragment;
    }

    public frgMainMenu() {
        // Required empty public constructor
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context= getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.frg_main_menu, container, false);
        mPhotoSize = getResources().getDimensionPixelSize(R.dimen.photo_size);
        mPhotoSpacing = getResources().getDimensionPixelSize(R.dimen.photo_spacing);

        photoGrid = (GridView) view.findViewById(R.id.albumGrid);

// initialize image adapter
//        imageAdapter = new ImageAdapter();
        Log.e(MainActivity.Tag, "frg Main 00000");
        mainmenudb db=new mainmenudb(this.getActivity());
        mPhotoSize =  getResources().getDimensionPixelSize(R.dimen.photo_size);
        mPhotoSpacing = getResources().getDimensionPixelSize(R.dimen.photo_spacing);
        maincursor= db.query(" t1." + mainmenudb.MainMenu_VisMain + "<>0", null);

        adp=new pjadpmainmenu(this.getActivity(), maincursor);
// set image adapter to the GridView
        photoGrid.setAdapter(adp);

// get the view tree observer of the grid and set the height and numcols dynamically
        photoGrid.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (adp.getNumColumns() == 0) {
                    final int numColumns = (int) Math.floor(photoGrid.getWidth() / (mPhotoSize + mPhotoSpacing));
                    if (numColumns > 0) {
                        final int columnWidth = (photoGrid.getWidth() / numColumns) - mPhotoSpacing;
                        adp.setNumColumns(numColumns);
                        adp.setItemHeight(columnWidth, columnWidth);

                    }
                }
            }
        });
        photoGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (maincursor.moveToPosition(position))
                    alertMessage(maincursor.getInt(maincursor.getColumnIndex(mainmenudb.MainMenu_ID)),
                            maincursor.getInt(maincursor.getColumnIndex(mainmenudb.MainMenu_SaveMe)),
                            maincursor.getString(maincursor.getColumnIndex(mainmenudb.MainMenu_Server)));

                return true;
            }
        });
        return view;
    }
    public void alertMessage(final long id, final int saveme, final String server) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked
                        mainmenudb db=new mainmenudb(getActivity());
                        db.setVisMainMenu(id, saveme, 0, server);
//                        MainActivity.changedata=true;
                        maincursor= db.query(String.format(" t1.%s <> 0 and t1.%s='%s'",
                                mainmenudb.MainMenu_VisMain, mainmenudb.MainMenu_Server, server), null);
                        adp.mcursor=maincursor;
                        adp.notifyDataSetChanged();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        // do nothing
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("آیا از حذف این گزینه مطمئن هستید؟")
                .setPositiveButton("بله", dialogClickListener)
                .setNegativeButton("خیر", dialogClickListener).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
/*        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    // ///////// ImageAdapter class /////////////////
    /*public class ImageAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private RelativeLayout.LayoutParams mImageViewLayoutParams;

        public ImageAdapter() {
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT);
        }

        public int getCount() {
            return CONTENT.length;
        }

        // set numcols
        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }

        // set photo item height
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, mItemHeight);
            notifyDataSetChanged();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {

            if (view == null)
                view = mInflater.inflate(R.layout.photo_item, null);

            ImageView cover = (ImageView) view.findViewById(R.id.mainitemimg);
            TextView title = (TextView) view.findViewById(R.id.mainitemtxt);

            cover.setLayoutParams(mImageViewLayoutParams);

// Check the height matches our calculated column width
            if (cover.getLayoutParams().height != mItemHeight) {
                cover.setLayoutParams(mImageViewLayoutParams);
            }

            cover.setImageResource(ICONS[position % ICONS.length]);
            title.setText(CONTENT[position % CONTENT.length]);

            return view;
        }
    }
*/

}

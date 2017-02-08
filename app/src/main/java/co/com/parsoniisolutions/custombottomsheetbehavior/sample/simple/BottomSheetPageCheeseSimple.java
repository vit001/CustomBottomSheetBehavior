package co.com.parsoniisolutions.custombottomsheetbehavior.sample.simple;

import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import co.com.parsoniisolutions.custombottomsheetbehavior.R;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.pager.BottomSheetPage;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.pager.BottomSheetPagerAdapter;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.pager.withloading.BottomSheetData;
import co.com.parsoniisolutions.custombottomsheetbehavior.sample.CheeseData;
import co.com.parsoniisolutions.custombottomsheetbehavior.sample.withloading.BottomSheetPagerAdapterCheeseWithLoading;
import co.com.parsoniisolutions.custombottomsheetbehavior.sample.PhotosPagerAdapter;


public class BottomSheetPageCheeseSimple extends BottomSheetPage {

    public BottomSheetPageCheeseSimple( LayoutInflater inflater, BottomSheetPagerAdapter bottomSheetPagerAdapter ) {
        super( inflater, bottomSheetPagerAdapter );
    }

    @Override
    public @LayoutRes int layoutRes() { return R.layout.bottom_pager_content; }

    private TextView  mTitle;
    private TextView  mSubTitle;
    private ViewPager mPhotosViewPager;

    @Override
    protected void initializeUI() {
        super.initializeUI();

        mTitle           = (TextView)inflatedView().findViewById( R.id.bottom_sheet_title );
        mSubTitle        = (TextView)inflatedView().findViewById( R.id.text_dummy1 );
        mPhotosViewPager = (ViewPager) inflatedView().findViewById( R.id.photos_view_pager );
    }

    @Override
    public void setUI( BottomSheetData bottomSheetData ) {
        CheeseData cheeseData = (CheeseData) bottomSheetData;
        mTitle.setText( cheeseData.getTitle() );
        mSubTitle.setText( cheeseData.getSubTitle() );

        PhotosPagerAdapter photos_adapter = new PhotosPagerAdapter( mPhotosViewPager.getContext(), cheeseData.getImageResourceList() );
        mPhotosViewPager.setAdapter(photos_adapter);
    }

    @Override
    protected BottomSheetData getBottomSheetData( int position ) {
        // Return the data for a pager position on UI thread
        BottomSheetPagerAdapterCheeseSimple adapter = (BottomSheetPagerAdapterCheeseSimple) pagerAdapter();
        if ( adapter == null )
            return null;
        CheeseData item = adapter.getItemAtPosition( position );
        return item;
    }

}
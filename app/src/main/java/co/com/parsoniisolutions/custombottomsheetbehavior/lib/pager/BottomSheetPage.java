package co.com.parsoniisolutions.custombottomsheetbehavior.lib.pager;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;

import co.com.parsoniisolutions.custombottomsheetbehavior.R;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.behaviors.BottomSheetBehaviorGoogleMapsLike;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.appbar.DelegatingMergedAppBarLayoutBehavior;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.behaviors.ScrollAwareFABBehavior;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.utils.DimensionUtils;
import co.com.parsoniisolutions.custombottomsheetbehavior.lib.appbar.DelegatingScrollingAppBarLayoutBehavior;

import java.lang.ref.WeakReference;


/**
 * A View Model for one page in a BottomSheet ViewPager.
 * The inflated view will get recycled for efficiency - we will only bind new data when needed.
 */
public class BottomSheetPage {

    public BottomSheetPage( LayoutInflater inflater, BottomSheetPagerAdapter bottomSheetPagerAdapter ) {
        mPagerAdapterRef = new WeakReference<>( bottomSheetPagerAdapter );
        mInflatedView = inflater.inflate( layoutRes(), null );
        initializeUI();

        mInflatedView.setTag( R.id.BOTTOM_SHEET_PAGE, this );
    }

    protected View                  mInflatedView;
    public    View inflatedView() { return mInflatedView; }

    public @LayoutRes int layoutRes() { throw new UnsupportedOperationException( "You must subclass BottomSheetPage and override layoutRes()" ); }

    protected int mPosition = -1;
    protected void setNewAdapterPosition( int position ) {
        mPosition = position;

        // Ask for new data synchronously
        BottomSheetData bottomSheetData = getBottomSheetData( position );
        setUI( bottomSheetData );
    }

    protected WeakReference<BottomSheetPagerAdapter> mPagerAdapterRef;
    protected BottomSheetPagerAdapter pagerAdapter() { return mPagerAdapterRef.get(); }

    //private   View mFabFloatingFrameLayout;
    protected View mNestedScrollView;

    public int getBottomSheetState() {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)mInflatedView.findViewById( R.id.bottom_sheet ).getLayoutParams();
        BottomSheetBehaviorGoogleMapsLike behavior = (BottomSheetBehaviorGoogleMapsLike)params.getBehavior();
        return behavior.getState();
    }

    public void setBottomSheetState( int newState, boolean noanim ) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)mInflatedView.findViewById( R.id.bottom_sheet ).getLayoutParams();
        BottomSheetBehaviorGoogleMapsLike behavior = (BottomSheetBehaviorGoogleMapsLike)params.getBehavior();
        behavior.setState( newState, noanim );
    }

    protected void initializeUI() {
        mNestedScrollView = mInflatedView.findViewById( R.id.bottom_sheet );
        setFabBehaviorParameters();
        setOnBottomSheetStateChangedListener();
        setDelegatingMergedToolbarParameters();
        setDelegatingScrollToolbarParameters();
    }

    public void setUI( BottomSheetData bottomSheetData ) { throw new UnsupportedOperationException( "You must subclass BottomSheetPage and override setUI()" ); }

    /**
     * Close any database cursors or perform any other cleanup necessary
     */
    public void onDestroy() { }

    /**
     * We need to observe the BottomSheet.
     * When user triggers a BottomSheet state change, other BottomSheets in the PagerAdapter should reflect this as well.
     */
    private void setOnBottomSheetStateChangedListener() {
        final BottomSheetPage bottomSheetPage = this;
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams)mInflatedView.findViewById( R.id.bottom_sheet ).getLayoutParams();
        BottomSheetBehaviorGoogleMapsLike behavior = (BottomSheetBehaviorGoogleMapsLike)params.getBehavior();
        behavior.addBottomSheetCallback( new BottomSheetBehaviorGoogleMapsLike.BottomSheetCallback() {
            @Override
            public void onStateChanged( @NonNull View bottomSheet, @BottomSheetBehaviorGoogleMapsLike.State int newState ) {
                if ( mPagerAdapterRef.get() != null ) {
                    mPagerAdapterRef.get().onBottomSheetStateChanged( newState, bottomSheetPage );
                }
            }

            @Override
            public void onSlide( @NonNull View bottomSheet, float slideOffset ) { }
        });
    }

    private void setFabBehaviorParameters() {
        FloatingActionButton ffl = (FloatingActionButton) mInflatedView.findViewById( R.id.fab );
        if ( ffl != null ) {
            int fabHeight = (int)ffl.getContext().getResources().getDimension( R.dimen.fab_size );

            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ffl.getLayoutParams();
            ScrollAwareFABBehavior behavior = (ScrollAwareFABBehavior)params.getBehavior();
            behavior.setOffsetValue( DimensionUtils.getToolbarHeight(ffl.getContext()) + fabHeight / 2 );
            params.setBehavior( behavior );
        }
    }

    private void setDelegatingMergedToolbarParameters() {
        View mergedAppBar = mInflatedView.findViewById( R.id.delegating_merged_appbarlayout );
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) mergedAppBar.getLayoutParams();
        DelegatingMergedAppBarLayoutBehavior behavior = (DelegatingMergedAppBarLayoutBehavior) params.getBehavior();
        behavior.setToolbarTop( DimensionUtils.getStatusBarHeight( mergedAppBar.getContext() ) );
        behavior.setToolbarBottom( DimensionUtils.getStatusBarHeight( mergedAppBar.getContext() ) + DimensionUtils.getToolbarHeight( mergedAppBar.getContext() ) );
        behavior.setParentBottomSheetPage( this );
    }

    private void setDelegatingScrollToolbarParameters() {
        View scrollAppBar = mInflatedView.findViewById( R.id.delegating_scroll_appbarlayout );
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) scrollAppBar.getLayoutParams();
        DelegatingScrollingAppBarLayoutBehavior behavior = (DelegatingScrollingAppBarLayoutBehavior) params.getBehavior();
        behavior.setParentBottomSheetPage( this );
    }

    /**
     * Returns true if this BottomSheetPage is currently shown (selected) in the ViewPager
     */
    public boolean isSelected() {
        if ( mPagerAdapterRef.get() == null ) {
            return false;
        }
        return mPagerAdapterRef.get().selectedPosition() == mPosition;
    }

    protected BottomSheetData getBottomSheetData( int position ) {
        throw new UnsupportedOperationException( "You must subclass BottomSheetPage and override getBottomSheetData()" );
    }
}
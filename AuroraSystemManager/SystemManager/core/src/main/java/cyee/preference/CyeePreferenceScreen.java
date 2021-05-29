package cyee.preference;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import com.cyee.utils.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import com.cyee.internal.R;
import cyee.changecolors.ChameleonColorManager;
import cyee.widget.CyeeListView;
import cyee.widget.CyeeWidgetResource;

/**
 * Represents a top-level {@link Preference} that
 * is the root of a Preference hierarchy. A {@link PreferenceActivity}
 * points to an instance of this class to show the preferences. To instantiate
 * this class, use {@link PreferenceManager#createPreferenceScreen(Context)}.
 * <ul>
 * This class can appear in two places:
 * <li> When a {@link PreferenceActivity} points to this, it is used as the root
 * and is not shown (only the contained preferences are shown).
 * <li> When it appears inside another preference hierarchy, it is shown and
 * serves as the gateway to another screen of preferences (either by showing
 * another screen of preferences as a {@link Dialog} or via a
 * {@link Context#startActivity(android.content.Intent)} from the
 * {@link Preference#getIntent()}). The children of this {@link PreferenceScreen}
 * are NOT shown in the screen that this {@link PreferenceScreen} is shown in.
 * Instead, a separate screen will be shown when this preference is clicked.
 * </ul>
 * <p>Here's an example XML layout of a PreferenceScreen:</p>
 * <pre>
&lt;PreferenceScreen
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:key="first_preferencescreen"&gt;
    &lt;CheckBoxPreference
            android:key="wifi enabled"
            android:title="WiFi" /&gt;
    &lt;PreferenceScreen
            android:key="second_preferencescreen"
            android:title="WiFi settings"&gt;
        &lt;CheckBoxPreference
                android:key="prefer wifi"
                android:title="Prefer WiFi" /&gt;
        ... other preferences here ...
    &lt;/PreferenceScreen&gt;
&lt;/PreferenceScreen&gt; </pre>
 * <p>
 * In this example, the "first_preferencescreen" will be used as the root of the
 * hierarchy and given to a {@link PreferenceActivity}. The first screen will
 * show preferences "WiFi" (which can be used to quickly enable/disable WiFi)
 * and "WiFi settings". The "WiFi settings" is the "second_preferencescreen" and when
 * clicked will show another screen of preferences such as "Prefer WiFi" (and
 * the other preferences that are children of the "second_preferencescreen" tag).
 * 
 * <div class="special reference">
 * <h3>Developer Guides</h3>
 * <p>For information about building a settings UI with Preferences,
 * read the <a href="{@docRoot}guide/topics/ui/settings.html">Settings</a>
 * guide.</p>
 * </div>
 *
 * @see PreferenceCategory
 */
public final class CyeePreferenceScreen extends CyeePreferenceGroup implements AdapterView.OnItemClickListener,
        DialogInterface.OnDismissListener {

    private static final String LOGTAG = "CyeePreferenceScreen";
    
    private ListAdapter mRootAdapter;
    
    private Dialog mDialog;

    private CyeeListView mListView;
    
    /**
     * Do NOT use this constructor, use {@link PreferenceManager#createPreferenceScreen(Context)}.
     * @hide-
     */
    public CyeePreferenceScreen(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.preferenceScreenStyle);
    }

    /**
     * Returns an adapter that can be attached to a {@link PreferenceActivity}
     * or {@link PreferenceFragment} to show the preferences contained in this
     * {@link PreferenceScreen}.
     * <p>
     * This {@link PreferenceScreen} will NOT appear in the returned adapter, instead
     * it appears in the hierarchy above this {@link PreferenceScreen}.
     * <p>
     * This adapter's {@link Adapter#getItem(int)} should always return a
     * subclass of {@link Preference}.
     * 
     * @return An adapter that provides the {@link Preference} contained in this
     *         {@link PreferenceScreen}.
     */
    public ListAdapter getRootAdapter() {
        if (mRootAdapter == null) {
            mRootAdapter = onCreateRootAdapter();
        }
        
        return mRootAdapter;
    }
    
    /**
     * Creates the root adapter.
     * 
     * @return An adapter that contains the preferences contained in this {@link PreferenceScreen}.
     * @see #getRootAdapter()
     */
    protected ListAdapter onCreateRootAdapter() {
        return new CyeePreferenceGroupAdapter(this, getContext(), true);
    }

    /**
     * Binds a {@link CyeeListView} to the preferences contained in this {@link PreferenceScreen} via
     * {@link #getRootAdapter()}. It also handles passing list item clicks to the corresponding
     * {@link Preference} contained by this {@link PreferenceScreen}.
     * 
     * @param listView The list view to attach to.
     */
    public void bind(CyeeListView listView) {
        listView.setOnItemClickListener(this);
        listView.setAdapter(getRootAdapter());
        /*weidong begin*/
        /*listView.setSelector(R.drawable.cyee_transparent);*/
        /*weidong end*/
        listView.setDivider(null);
        
        onAttachedToActivity();
    }
    
    @Override
    protected void onClick() {
        if (getIntent() != null || getFragment() != null || getPreferenceCount() == 0) {
            return;
        }
        
        showDialog(null);
    }
    
    private void showDialog(Bundle state) {
        Context context = getContext();
        if (mListView != null) {
            mListView.setAdapter(null);
        }

        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View childPrefScreen = inflater.inflate(
                    com.cyee.internal.R.layout.cyee_preference_list_fragment, null);
        mListView = (CyeeListView) childPrefScreen.findViewById(android.R.id.list);
        bind(mListView);

        // Set the title bar if title is available, else no title bar
        final CharSequence title = getTitle();
        Dialog dialog = mDialog = new Dialog(context, context.getThemeResId());
        if (TextUtils.isEmpty(title)) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        } else {
            dialog.setTitle(title);
        }
        dialog.setContentView(childPrefScreen);        
        dialog.setOnDismissListener(this);
        if (state != null) {
            dialog.onRestoreInstanceState(state);
        }

        // Add the screen to the list of preferences screens opened as dialogs
        getPreferenceManager().addPreferencesScreen(dialog);
        // Gionee <weidong> <2015-04-26> modify for CR01458298 begin
        if (ChameleonColorManager.isNeedChangeColor(context)) {
            dialog.getWindow().setBackgroundDrawable(
                    new ColorDrawable(ChameleonColorManager.getBackgroudColor_B1()));
        }
        // Gionee <weidong> <2015-04-26> modify for CR01458298 end
        dialog.show();
    }
    
    public void onDismiss(DialogInterface dialog) {
        mDialog = null;
        getPreferenceManager().removePreferencesScreen(dialog);
    }
    
    /**
     * Used to get a handle to the dialog. 
     * This is useful for cases where we want to manipulate the dialog
     * as we would with any other activity or view.
     */
    public Dialog getDialog() {
        return mDialog;
    }

    public void onItemClick(AdapterView parent, View view, int position, long id) {
        // If the list has headers, subtract them from the index.
        if (parent instanceof CyeeListView) {
            position -= ((CyeeListView) parent).getHeaderViewsCount();
        }
        Object item = getRootAdapter().getItem(position);
        if (!(item instanceof CyeePreference)) return;
        
        final CyeePreference preference = (CyeePreference) item;
        Log.d(LOGTAG, "onItemClick performClick");
        preference.performClick(this);
    }

    @Override
    protected boolean isOnSameScreenAsChildren() {
        return false;
    }
    
    @Override
    protected Parcelable onSaveInstanceState() {
        final Parcelable superState = super.onSaveInstanceState();
        final Dialog dialog = mDialog;
        if (dialog == null || !dialog.isShowing()) {
            return superState;
        }
        
        final SavedState myState = new SavedState(superState);
        myState.isDialogShowing = true;
        myState.dialogBundle = dialog.onSaveInstanceState();
        return myState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state == null || !state.getClass().equals(SavedState.class)) {
            // Didn't save state for us in onSaveInstanceState
            super.onRestoreInstanceState(state);
            return;
        }
         
        SavedState myState = (SavedState) state;
        super.onRestoreInstanceState(myState.getSuperState());
        if (myState.isDialogShowing) {
            showDialog(myState.dialogBundle);
        }
    }
    
    private static class SavedState extends BaseSavedState {
        boolean isDialogShowing;
        Bundle dialogBundle;
        
        public SavedState(Parcel source) {
            super(source);
            isDialogShowing = source.readInt() == 1;
            dialogBundle = source.readBundle();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(isDialogShowing ? 1 : 0);
            dest.writeBundle(dialogBundle);
        }

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
    
}
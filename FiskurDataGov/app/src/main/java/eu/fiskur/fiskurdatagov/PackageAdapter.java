package eu.fiskur.fiskurdatagov;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObject;
import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObjectResource;

/**
 * Created by jonathan.fisher on 20/01/2015.
 */
public class PackageAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater;
    List<PackageSearchResultObject> packages = Collections.emptyList();

    public PackageAdapter(Context context, List<PackageSearchResultObject> packages){
        this.context = context;
        this.packages = packages;
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        return packages.size();
    }

    @Override
    public Object getItem(int position) {
        return packages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.package_row, parent, false);
        }

        PackageSearchResultObject packageObj = packages.get(position);

        TextView packageLabel = ViewHolder.get(convertView, R.id.package_label);
        packageLabel.setText(packageObj.getTitle());

        TextView organisationLabel = ViewHolder.get(convertView, R.id.organisation_label);
        organisationLabel.setText(packageObj.getOrganization().getTitle());


        return convertView;
    }

    static class ViewHolder {
        @SuppressWarnings("unchecked")
        public static <T extends View> T get(View view, int id) {
            SparseArray<View> viewHolder = (SparseArray<View>) view.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<View>();
                view.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = view.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }
    }
}


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

import eu.fiskur.fiskurdatagov.objects.PackageSearchResultObjectResource;

/**
 * Created by jonathan.fisher on 20/01/2015.
 */
public class ResourceAdapter extends BaseAdapter {

    Context context;
    private LayoutInflater inflater;
    List<PackageSearchResultObjectResource> resources = Collections.emptyList();

    public ResourceAdapter(Context context, List<PackageSearchResultObjectResource> resources){
        this.context = context;
        this.resources = resources;
        inflater = ((Activity)context).getLayoutInflater();
    }

    @Override
    public int getCount() {
        return resources.size();
    }

    @Override
    public Object getItem(int position) {
        return resources.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.resource_row, parent, false);
        }

        PackageSearchResultObjectResource res = resources.get(position);

        TextView resourceLabel = ViewHolder.get(convertView, R.id.resource_label);
        resourceLabel.setText(res.toString());

        ImageView resourceImage = ViewHolder.get(convertView, R.id.resource_type);
        String format = res.getFormat().toLowerCase();
        if(format.isEmpty()){
            resourceImage.setVisibility(View.GONE);
        }else if(format.equals("xls")){
            resourceImage.setVisibility(View.VISIBLE);
            resourceImage.setImageResource(R.drawable.ic_excel);
        }else if(format.equals("zip")){
            resourceImage.setVisibility(View.VISIBLE);
            resourceImage.setImageResource(R.drawable.ic_zip);
        }else if(format.equals("pdf")){
            resourceImage.setVisibility(View.VISIBLE);
            resourceImage.setImageResource(R.drawable.ic_pdf);
        }


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

package com.grupo214.usuario.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LineasAdapter extends BaseExpandableListAdapter {

    private static String TAG = "LineasAdapter";
    private Context _context;
    private List<Linea> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Ramal>> _listDataTextChild;

    public LineasAdapter(Context context, List<Linea> mLineas) {
        this._context = context;
        this._listDataHeader = new ArrayList<>();
        this._listDataTextChild = new HashMap<>();

//  ACA LLEGA NULL, DEBO VALIDAR PORQUE; DEBERIA LLEGAR SIEMPRE ALGO VALIDO A ESTA PARTE :(
// creo que solo es cuando toco el truenito :3
        for (Linea l : mLineas) {
            this._listDataHeader.add(l);
            this._listDataTextChild.put(l.getLinea(), l.getRamales());
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataTextChild.get(this._listDataHeader.get(groupPosition).getLinea())
                .get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Ramal r = (Ramal) getChild(groupPosition, childPosition);
        Log.d(TAG,r.toString());

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.ramal_fila, null);
        }

        TextView tx_ramal = (TextView) convertView
                .findViewById(R.id.list_text_ramal);

        CheckBox checkBox = (CheckBox) convertView
                .findViewById(R.id.list_checkBox);

        tx_ramal.setText(r.getDescripcion());
        checkBox.setChecked(r.isCheck());

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataTextChild.get(this._listDataHeader.get(groupPosition).getLinea())
                .size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final Linea l = (Linea) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.linea_fila, null);
        }
        Boolean flag = false;
        for (Ramal r : l.getRamales())
            if (r.isCheck()) {
                flag = true;
                break;
            }
        ImageView check = (ImageView) convertView.findViewById(R.id.ic_check_linea);

        if (flag) check.setVisibility(View.VISIBLE);
        else check.setVisibility(View.INVISIBLE);

        TextView tx_linea = (TextView) convertView
                .findViewById(R.id.header_text_linea);
        tx_linea.setTypeface(null, Typeface.BOLD);
        tx_linea.setText(l.getLinea());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

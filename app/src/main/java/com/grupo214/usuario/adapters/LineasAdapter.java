package com.grupo214.usuario.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.grupo214.usuario.R;
import com.grupo214.usuario.objects.Linea;
import com.grupo214.usuario.objects.Ramal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LineasAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private HashMap<String, List<Ramal>> _listDataTextChild;

    public LineasAdapter(Context context, List<Linea> mLineas) {
        this._context = context;
        this._listDataHeader = new ArrayList<>();
        this._listDataTextChild = new HashMap<>();

//  ACA LLEGA NULL, DEBO VALIDAR PORQUE; DEBERIA LLEGAR SIEMPRE ALGO VALIDO A ESTA PARTE :(
        for (Linea l : mLineas) {
            this._listDataHeader.add(l.getLinea());
            this._listDataTextChild.put(l.getLinea(), l.getRamales());
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataTextChild.get(this._listDataHeader.get(groupPosition))
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
        return this._listDataTextChild.get(this._listDataHeader.get(groupPosition))
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
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.linea_fila, null);
        }

        TextView tx_linea = (TextView) convertView
                .findViewById(R.id.header_text_linea);
        tx_linea.setTypeface(null, Typeface.BOLD);
        tx_linea.setText(headerTitle);

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

    public void setMlineas(ArrayList<Linea> mLineas) {
        for (Linea l : mLineas) {
            this._listDataHeader.add(l.getLinea());
            this._listDataTextChild.put(l.getLinea(), l.getRamales());
        }
    }
}

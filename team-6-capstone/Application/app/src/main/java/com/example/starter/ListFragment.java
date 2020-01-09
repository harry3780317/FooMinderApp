package com.example.starter;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.starter.database.FooMinderRepository;
import com.example.starter.database.dao.ReciptDataDAO;
import com.example.starter.database.dao.ReciptDataDAO_Impl;
import com.example.starter.database.entity.DateConverter;
import com.example.starter.database.entity.ReciptData;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ListFragment extends Fragment {
    FooMinderRepository mFooMinderRepository;
    List<ReciptData> receiptDataList;
    //View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mFooMinderRepository = new FooMinderRepository(getActivity().getApplication());
        receiptDataList = mFooMinderRepository.getReciptData();
        Collections.sort(receiptDataList);

        for(ReciptData reciptData : receiptDataList)
        {
            Log.d("ItemName",reciptData.getItem());
            Log.d("ItemDate", reciptData.getDate().toString());
            Log.d("ItemQuanity", String.valueOf(reciptData.getQuantity()));
            Log.d("ItemStoringTime", String.valueOf(reciptData.getStoringTime()));
            Log.d("ItemPrimaryKey", String.valueOf(reciptData.getUid()));
            Log.d("ItemExpiryDate", reciptData.getExpirydate().toString());
        }


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TableLayout dynamicTableLayout = (TableLayout) view.findViewById(R.id.dynamic_tablelayout);
        dynamicTableLayout.setGravity(Gravity.TOP);



        TableRow heading = new TableRow(dynamicTableLayout.getContext());
        heading.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        heading.setBackgroundColor(Color.parseColor("#6600FF00"));
        //heading.setWeightSum(4);
        //heading.setGravity(Gravity.LEFT);

        TextView attrib1 = new TextView(getActivity());
        attrib1.setTextColor(Color.BLACK);
        //attrib1.setGravity(Gravity.LEFT);
        attrib1.setTextSize(20);
        attrib1.setText("Date");

        TextView attrib2 = new TextView(getActivity());
        attrib2.setTextColor(Color.BLACK);
        //attrib2.setGravity(Gravity.LEFT);
        attrib2.setTextSize(20);
        attrib2.setText("Name");

        TextView attrib3 = new TextView(getActivity());
        attrib3.setTextColor(Color.BLACK);
        //attrib3.setGravity(Gravity.LEFT);
        attrib3.setTextSize(20);
        attrib3.setText("Qty");

        TextView attrib4 = new TextView(getActivity());
        attrib4.setTextColor(Color.BLACK);
        //attrib4.setGravity(Gravity.LEFT);
        attrib4.setTextSize(20);
        attrib4.setText("Duration");

        TextView attrib5 = new TextView(getActivity());
        attrib5.setTextColor(Color.BLACK);
        attrib5.setGravity(Gravity.RIGHT);
        attrib5.setTextSize(20);
        attrib5.setText("+/-");

        heading.addView(attrib1);
        heading.addView(attrib2);
        heading.addView(attrib3);
        heading.addView(attrib4);
        heading.addView(attrib5);
        dynamicTableLayout.addView(heading);

        for(ReciptData receiptData : receiptDataList)
        {

            TableRow tr= new TableRow(dynamicTableLayout.getContext());
            tr.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            Calendar expiryDate = toCalendar(receiptData.getExpirydate().getTime());
            Calendar presentDate = toCalendar(new Date().getTime());

            long ms1 = expiryDate.getTimeInMillis();
            long ms2 = presentDate.getTimeInMillis();

            long diff = ms1 - ms2;

            int daysdiff = (int)(diff / (24 * 60 * 60 * 1000));
            Log.d("days diff is", String.valueOf(daysdiff));
            if(daysdiff <= 3 && daysdiff >= 1)
                tr.setBackgroundColor(Color.parseColor("#26FF0000"));
            else if(daysdiff <= 0)
            {
                mFooMinderRepository.delete(receiptData);
                continue;
            }
            else
                tr.setBackgroundColor(Color.parseColor("#2600FF00"));
            //tr.setGravity(Gravity.LEFT);
            TextView cell = new TextView(tr.getContext());
            cell.setTextColor(Color.BLACK);

            //cell.setGravity(Gravity.LEFT);
            cell.setTextSize(18);
            cell.setText(millisecToDate(receiptData.getDate()));

            TextView cell1 = new TextView(tr.getContext());
            cell1.setTextColor(Color.BLACK);
            //cell1.setGravity(Gravity.LEFT);
            cell1.setTextSize(18);
            cell1.setText(receiptData.getItem());

            TextView cell2 = new TextView(tr.getContext());
            cell2.setTextColor(Color.BLACK);
            //cell2.setGravity(Gravity.LEFT);
            cell2.setTextSize(18);
            cell2.setText(String.valueOf(receiptData.getQuantity()));

            TextView cell3 = new TextView(tr.getContext());
            cell3.setTextColor(Color.BLACK);
            //cell3.setGravity(Gravity.LEFT);
            cell3.setTextSize(18);
            cell3.setText(String.valueOf(receiptData.getStoringTime()));



            Button incBtn = new Button(tr.getContext());
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT);
            incBtn.setLayoutParams(new TableRow.LayoutParams(lp.width, lp.height));
            incBtn.setText("+");
            incBtn.setTextSize(15);
            incBtn.setId(receiptData.getUid());
            incBtn.setOnClickListener(mListener);

            Button decBtn = new Button(tr.getContext());
            decBtn.setLayoutParams(new TableRow.LayoutParams(lp.width, lp.height));
            decBtn.setText("-");
            decBtn.setTextSize(15);
            decBtn.setId(receiptData.getUid());
            decBtn.setOnClickListener(mListener);

            tr.addView(cell);
            tr.addView(cell1);
            tr.addView(cell2);
            tr.addView(cell3);
            tr.addView(incBtn);
            tr.addView(decBtn);

            dynamicTableLayout.addView(tr);
        }
    }

    private Calendar toCalendar(long timestamp)
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private String millisecToDate(Date date)
    {
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("MM/dd/yy");
        String dateString = outputDateFormat.format(date.getTime());
        return dateString;
    }

    private View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TableRow tr = (TableRow) v.getParent();
            TextView cell = (TextView) tr.getChildAt(2);
            Button btn = (Button) v;
            int currQty = Integer.valueOf(cell.getText().toString());
            if(btn.getText() == "+")
            {

                currQty++;
                cell.setText(String.valueOf(currQty));
            }
            else {

                currQty--;
                if(currQty < 0)
                    currQty = 0;
                cell.setText(String.valueOf(currQty));
            }
            mFooMinderRepository.updateByColumnID(currQty, btn.getId());
        }
    };
}

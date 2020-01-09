package com.example.starter;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.app.usage.ExternalStorageStats;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.room.Database;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;
import com.google.firebase.ml.vision.text.RecognizedLanguage;
import org.atteo.evo.inflector.English;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.starter.database.FooMinderRepository;
import com.example.starter.database.FooMinderDatabase;
import com.example.starter.database.entity.FridgeStatusData;
import com.example.starter.database.entity.ReciptData;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AlertDialogLayout;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.parser.Tag;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;


public class RecordFragment extends Fragment{

//
    private ReciptData mCurrentReciptData;
    private FooMinderRepository mFooMinderRepository ;
//            = new FooMinderRepository((Application)getActivity().getApplication());

    static final int PERMISSION_REQUEST_TAKE_PHOTO = 1;
    static final int PERMISSION_REQUEST_IMAGE_FROM_GALLERY = 2;
    static final int PERMISSION_REQUEST_CAMERA = 3;
    static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 5;

    static final int PERMISSION_MULTIPLE = 6;

    List<ReciptData> receiptDataList;

    final String[] multiplePermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };

    ArrayList<String> item_list;

    ArrayAdapter<String> adapter;

    EditText quantityText;
    TextView date_in_record;
    AppCompatAutoCompleteTextView itemText;

    Button addButton;
    Button save_record;
    Button pulldata_record;


    ListView lv;
    List<dataClass> itemList;


    private TextView displaydate;
    private DatePickerDialog.OnDateSetListener dispalydatesetlistener;
    private AppCompatAutoCompleteTextView autoTextView;
    private String[] fruits = {"apple", "apricot", "avocados", "banana", "blueberry", "cantaloupe", "cherry", "coconut", "fig", "grapefruit", "grape", "honeydew", "kiwi", "lemon", "lime", "mango", "olive", "orange", "papaya", "peach", "pear", "pineapple", "pomegranate", "pumpkin", "strawberry", "tomatoes", "watermelon", "artichoke", "asparagus", "broccoli", "cabbage", "carrot", "cauliflower", "celery", "cucumber","eggplant", "garlic", "kale", "lemongrass", "lettuce", "mushroom", "onion", "parsnip", "pepper", "pickle", "potato", "spinach", "squash", "zucchini"};

    static final String fruitwebUrl = "http://www.eatbydate.com/fruits";
    static final String vegetablewebUrl = "http://www.eatbydate.com/vegetables";

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record, container, false);

        lv = (ListView) view.findViewById(R.id.ListView);
        itemText =  view.findViewById(R.id.addtext);
        quantityText = (EditText) view.findViewById(R.id.record_quantity);
        date_in_record = (TextView) view.findViewById(R.id.record_date);
        autoTextView =  view.findViewById(R.id.addtext);
        pulldata_record = (Button)view.findViewById(R.id.record_pulldata);
        addButton = (Button) view.findViewById(R.id.addbutton);
        save_record = view.findViewById(R.id.record_save);




        final ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),android.R.layout.select_dialog_item,fruits);
        autoTextView.setThreshold(1);
        autoTextView.setAdapter(adapter1);

        item_list = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_multiple_choice,item_list);
        itemList = new ArrayList<>();

        View.OnClickListener addlisner = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> namelist = Arrays.asList(date_in_record.getText().toString(), itemText.getText().toString(),quantityText.getText().toString());
                Date stringDate = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
                try{
                    stringDate = dateFormat.parse(date_in_record.getText().toString());
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
                dataClass newRecord = new dataClass();
                newRecord.setItemDate(stringDate);
                newRecord.setItemName(itemText.getText().toString());
                newRecord.setQuantity(Integer.valueOf(quantityText.getText().toString()));
                itemList.add(newRecord);
                item_list.add(namelist.toString());
                adapter.notifyDataSetChanged();
            }
        };
        date_in_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        getActivity(),
                        android.R.style.Theme_Holo_Dialog_MinWidth,
                        dispalydatesetlistener,
                        year,month,day);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

//        displaydate = (TextView) view.findViewById(R.id.record_date);
        dispalydatesetlistener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month+1;
                year = year - 2000;

                Log.d(getTag(),"onDateSet: mm/dd/yy"+ month +"/" + day +"/" +year);
                String setdate = month +"/" + day +"/" +year;
                date_in_record.setText(setdate);

            }
        };

        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                SparseBooleanArray positionchecker = lv.getCheckedItemPositions();
                int count = lv.getCount();
                for (int item = count-1; item>=0;item--){
                    if(positionchecker.get(item)){
                        adapter.remove(item_list.get(item));
                        itemList.remove(itemList.get(item));
                    }
                }
                positionchecker.clear();
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        addButton.setOnClickListener(addlisner);

        lv.setAdapter(adapter);

        pulldata_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(dataClass item: itemList)
                {
                    Log.d("the item is", item.getItemDate().toString());
                }
                if(!itemList.isEmpty()) {

                    web_data data = new web_data();
                    data.execute();
                    Toast.makeText(getActivity(), "Pull data succeed", Toast.LENGTH_SHORT).show();
                }
                 else {
                    Toast.makeText(getActivity(), "Cannot pull data because no fruit/vegetable words are recognized, please enter items manually", Toast.LENGTH_SHORT).show();
                }
            }
        });


        save_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemList.isEmpty())
                {
                    for(dataClass iteeem : itemList)
                    {
                            mCurrentReciptData = new ReciptData(iteeem.getItemName(), iteeem.getItemQuantity(), iteeem.getItemExpiryDuration());
                            mCurrentReciptData.setDate(iteeem.getItemDate());
                            mCurrentReciptData.setExpirydate(iteeem.getItemExpiryDate());
                            Log.d("expiry date is",iteeem.getItemExpiryDate().toString());
                            mFooMinderRepository.insert(mCurrentReciptData);
                    }
                    Toast.makeText(getActivity(), "save succeed", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "No data can be saved because item list is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }
    private class web_data extends AsyncTask<Void,Void,Void> {
        /*ProgressDialog progressD = new ProgressDialog(getActivity()) {

            public void onPreExecute() {
                web_data.super.onPreExecute();
                progressD.setMessage("Fetching data from " + fruits);
                progressD.show();
            }
        };
        @Override
        public Void doInBackground(Void... voids) {
            int numberofLinksAdded = getItemLinks(10, fruitwebUrl,vegetablewebUrl);
            getItemDurations(numberofLinksAdded, 60);
            for (dataClass item1 : itemList) {
                Log.d("dateofPurchase", item1.getItemDate().toString());
                Log.d("newExpiryDate", String.valueOf(item1.getItemExpiryDuration()));
            }
            return null;
        };
        @Override
        public void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (progressD.isShowing()) {
                progressD.dismiss();
            }
        };*/
        public ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show loading progress
            progress = new ProgressDialog(getContext());
            progress.setMessage("Fetching data from " + fruitwebUrl + " and " + vegetablewebUrl);
            progress.show();
        }

        @Override
        protected Void doInBackground(Void... voids)  {
            int numberofLinksAdded = getItemLinks(10, fruitwebUrl, vegetablewebUrl);
            getItemDurations(numberofLinksAdded, 60);
            for(dataClass item1 : itemList)
            {
                Log.d("dateofPurchase", item1.getItemDate().toString());
                Log.d("newExpiryDate", String.valueOf(item1.getItemExpiryDuration()));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(progress.isShowing())
            {
                progress.dismiss();
            }
            //Log.d("SCRAP RESULTS",document.outerHtml()); //display on logcat for now.
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mFooMinderRepository = new FooMinderRepository(getActivity().getApplication());
        receiptDataList = mFooMinderRepository.getReciptData();

        for(ReciptData reciptData : receiptDataList)
        {
            Log.d("ItemName",reciptData.getItem());
            Log.d("ItemDate", reciptData.getDate().toString());
            Log.d("ItemQuanity", String.valueOf(reciptData.getQuantity()));
            Log.d("ItemStoringTime", String.valueOf(reciptData.getStoringTime()));
            Log.d("ItemPrimaryKey", String.valueOf(reciptData.getUid()));
        }
    }

    private int getItemLinks(int timeOutSec,  String... urls)
    {
        Document doc;
        Elements elements;
        int numberofLinksAdded = 0;
        for(String url : urls)
        {
            try
            {
                doc = Jsoup.connect(url).timeout(timeOutSec * 1000).get();
                elements = doc.getElementsByClass("row margin-topbot-15").first().getElementsByTag("a");
                for(Element element : elements)
                {
                    String eatbyDateItemName = element.text().toLowerCase();
                    if(element.text().equals("Onions"))
                    {
                        Log.d("DUDE THIS IS", eatbyDateItemName);
                    }
                    for(dataClass item : itemList)
                    {
                        String itemToSearch = item.getItemName();
                        Log.d("getItemLinks itemName", itemToSearch);
                        if(eatbyDateItemName.equals(itemToSearch) || eatbyDateItemName.equals(English.plural(itemToSearch))){
                            numberofLinksAdded++;
                            item.setItemLink(element.attr("href"));
                        }
                    }
                }
            }
            catch (IOException  e)
            {
                e.printStackTrace();
                Log.d("ioexception caught", "possiblly failed to connect to link");
            }
        }
        if(numberofLinksAdded == 0)
        {
            Log.d("noitems", "no itemames matches to eatbydate itemnames");
        }
        return numberofLinksAdded;
    }

    private void getItemDurations(int numberofLinksToSearch, int timeOutSec)
    {
        Document doc;
        if(numberofLinksToSearch == 0)
        {
            Log.d("0Links", "no links provided to search");
            return;
        }
        for(dataClass item: itemList){
            Log.d("link to search", item.getItemLink());
            try
            {
                doc = Jsoup.connect(item.getItemLink()).timeout(timeOutSec * 1000).get();
                Elements rows = doc.getElementsByClass("row margin-topbot-15 cody-green-table").first().getElementsByTag("tr");
                boolean foundColIndex = false;
                int indexOfTargetCol = 0;
                for (int j = 0; j < rows.size(); j++) {
                    Elements cells = rows.get(j).children();
                    if(!foundColIndex)
                    {
                        indexOfTargetCol = 0;
                        for (int k = 0; k<cells.size(); k++) {
                            if (cells.get(k).text().toLowerCase().contains("refrigerator") || cells.get(k).text().toLowerCase().contains("fridge")) {
                                foundColIndex = true;
                                //Log.d("name fields", cells.get(k).text());
                                break;
                            }
                            indexOfTargetCol = indexOfTargetCol + 1;
                        }
                    }
                    else if(cells.get(0).hasText()){
                        setExpiryDate(item, cells.get(indexOfTargetCol).text());
                        break;
                    }
                }
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private void setExpiryDate(dataClass item, String duration)
    {
        int days_difference = 0;
        Pattern pattern = Pattern.compile("(\\d+) ((?i)day|week|month|year)");
        Matcher matcher = pattern.matcher(duration);
        if(matcher.find())
        {
            String[] newDate = new String[2];
            newDate[0] = matcher.group(1).toLowerCase();
            newDate[1] = matcher.group(2).toLowerCase();
            Calendar c = Calendar.getInstance();
            c.setTime(item.getItemDate());
            int timeMask = 0;
            int timeDuration = 0;
            if (newDate[1].toLowerCase().equals("day") || newDate[1].toLowerCase().equals("week") )
            {
                timeMask = Calendar.DATE;
            }
            else if(newDate[1].toLowerCase().equals("month"))
            {
                timeMask = Calendar.MONTH;
            }
            else if(newDate[1].toLowerCase().equals("year"))
            {
                timeMask = Calendar.YEAR;
            }
            if(newDate[1].toLowerCase().equals("week"))
            {
                timeDuration = Integer.valueOf(newDate[0]) * 7;
            }else {
                timeDuration = Integer.valueOf(newDate[0]);
            }
            c.add(timeMask, timeDuration);
            Date storingDuration = c.getTime();
            int days_conversion = 1000 * 60 * 60 * 24;
            days_difference = (int)((storingDuration.getTime() - item.getItemDate().getTime() )/(days_conversion));
            Log.d("duration is", String.valueOf(days_difference));
            item.setExpiryDate(storingDuration);
            Log.d("setItemExpiryDate", item.getItemExpiryDate().toString());
            item.setExpiryDuration(days_difference);
            Log.d("setItemExpiry", String.valueOf(item.getItemExpiryDuration()));
        }

    }

}

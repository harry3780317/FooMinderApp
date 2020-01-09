package com.example.starter;

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
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.example.starter.database.FooMinderRepository;
import com.example.starter.database.FooMinderDatabase;
import com.example.starter.database.entity.FridgeStatusData;
import com.example.starter.database.entity.ReciptData;


public class activity_third extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener {

    // Database
    private ReciptData mCurrentReciptData;
    //FridgeStatusData mCurrentFridgeStatusData;
    //FooMinderDatabase mFooMinderDatabase;
    private FooMinderRepository mFooMinderRepository = new FooMinderRepository(getApplication());

    static final int PERMISSION_REQUEST_TAKE_PHOTO = 1;
    static final int PERMISSION_REQUEST_IMAGE_FROM_GALLERY = 2;
    static final int PERMISSION_REQUEST_CAMERA = 3;
    static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 4;
    static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 5;

    static final int PERMISSION_MULTIPLE = 6;

    final String[] multiplePermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
    };

    static final String fruitwebUrl = "http://www.eatbydate.com/fruits";
    static final String vegetablewebUrl = "http://www.eatbydate.com/vegetables";

    String[]itemNames;
    List<dataClass> itemList;
    String currentPhotoPath;
    ImageView urpic;
    TextView convertedText;
    Bitmap image;
    Button pullData;
    Button convert;
    Button save;
    Uri imageFileUri;

    TextView userName;

    // Database
//    private ReciptData mCurrentReciptData;
    private FridgeStatusData mCurrentFridgeStatusData;
    private FooMinderDatabase mFooMinderDatabase;
//    private FooMinderRepository mFooMinderRepository;

    // Navigation Drawer
    private DrawerLayout mDrawerlayout;
    private ActionBarDrawerToggle mToggle;

    private Toolbar mToolbar;

    // Bottom Navigation
    private BottomNavigationView mBottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        itemNames = getResources().getStringArray(R.array.fruit_vegetable_names);
        itemList = new ArrayList<>();

        mToolbar = (Toolbar) findViewById(R.id.nav_actionBar);
        setSupportActionBar(mToolbar);

        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_view);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.bottom_home:
                        Intent homeIntent = new Intent(getApplicationContext(), activity_third.class);
                        startActivity(homeIntent);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
                        break;

                    case R.id.bottom_scan:
                        checkMultiplePermissions(multiplePermissions);
                        break;

                    case R.id.bottom_record:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecordFragment()).commit();
                        break;

                    case R.id.bottom_fridge:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FridgeFragment()).commit();
                        break;

                    case R.id.bottom_settings:
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                        break;

                        default:
                            return false;
                }
                return false;
            }
        });

        mToggle = new ActionBarDrawerToggle(this, mDrawerlayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        mDrawerlayout.addDrawerListener(mToggle);
        mToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        Intent intent = getIntent();
//        String NameofUser = ((Intent) intent).getStringExtra("namekey");

//        userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.mainMenu);
//        userName.setText(NameofUser);
//
//        String GreetStr = "Hello! " + NameofUser + "\r\n\r\n" + "Please press the button to scan your receipt.";

//        TextView GreetText = (TextView) findViewById(R.id.username);
//        GreetText.setText(GreetStr);

//        Button scan = (Button) findViewById(R.id.startCapture);
//        Button viewlist = (Button) findViewById(R.id.viewList);

        pullData = (Button)findViewById(R.id.pullData);
        convert =(Button)findViewById(R.id.convertimagetotext);
//        urpic  = (ImageView)findViewById(R.id.tryimageview);
        save = (Button)findViewById(R.id.save);
        convertedText = (TextView)findViewById(R.id.imagetotext);
        convertedText.setMovementMethod(new ScrollingMovementMethod());

        convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runTextRecognition(image);
            }
        });

        pullData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemList.isEmpty()){
                    pullData.setEnabled(false);
                    webData data = new webData();
                    data.execute();
                    Toast.makeText(getApplicationContext(), "Sucessfully pulled data", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Cannot pull data because no fruit/vegetable words are recognized, please enter items manually", Toast.LENGTH_SHORT).show();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!itemList.isEmpty())
                {
                    for(dataClass item : itemList)
                    {
                        Log.d("itemName isssss", item.getItemName());
                        mCurrentReciptData = new ReciptData(item.getItemName(), item.getItemQuantity(), item.getItemExpiryDuration());
                        mCurrentReciptData.setDate(item.getItemDate());
                        mCurrentReciptData.setExpirydate(item.getItemExpiryDate());
                        mFooMinderRepository.insert(mCurrentReciptData);
                    }
                    Toast.makeText(getApplicationContext(), "Sucessfully saved data", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "No data can be saved because item list is empty", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        scan.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                checkMultiplePermissions(multiplePermissions);
//            }
//        });

//        Button recordBtn = (Button) findViewById(R.id.recordBtn);

//        recordBtn.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent recordIntent = new Intent(getApplicationContext(), RecordActivity.class);
//                startActivity(recordIntent);
//            }
//        }));

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_scan:
                checkMultiplePermissions(multiplePermissions);
                Toast.makeText(this, "In Scan", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_list:
//                Intent listIntent = new Intent(getApplicationContext(), activity_third.class);
//                startActivity(listIntent);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListFragment()).commit();
                Toast.makeText(this, "In List", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_record:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecordFragment()).commit();
                Toast.makeText(this, "In Record", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_fridge:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new FridgeFragment()).commit();
                Toast.makeText(this, "In fridge", Toast.LENGTH_SHORT).show();
                break;

//            case R.id.nav_switch:
//                Intent switchIntent = new Intent(getApplicationContext(), activity_secondary.class);
//                startActivity(switchIntent);
//                Toast.makeText(this, "In Switch", Toast.LENGTH_SHORT).show();

            case R.id.nav_setting:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingFragment()).commit();
                Toast.makeText(this, "In Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_help:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HelpFragment()).commit();
                Toast.makeText(this, "In Help", Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_about:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutUsFragment()).commit();
                Toast.makeText(this, "In About Us", Toast.LENGTH_SHORT).show();
                break;
        }
        mDrawerlayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void checkMultiplePermissions(final String[] permissionList) //refactored
    {
        List<String> nonGrantedPermissions  = new ArrayList<>();

        for(String permission : permissionList)
        {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                nonGrantedPermissions.add(permission); //add permission to be granted
            }
        }
        if(!nonGrantedPermissions.isEmpty())
        {
            String[] requestPermissions = new String[nonGrantedPermissions.size()]; //convert list of string to array of string
            requestPermissions = nonGrantedPermissions.toArray(requestPermissions);
            ActivityCompat.requestPermissions(this , requestPermissions ,PERMISSION_MULTIPLE ); //pass in permission list for request
        }
        else {

            startCamera();
        }
    }

    /*private void checkCameraPermission()
    {
        // ensure api level is >= 16
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // check the permission of camera usage
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                //start the intent for image capture
                startCamera();

            } else {
                //request the permission to use camera
                ActivityCompat.requestPermissions(activity_third.this, new String[]{Manifest.permission.CAMERA}, PERMISSION_REQUEST_CAMERA);
            }
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) { //callback
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //handle the requested permission here
        boolean allGranted = true;
        if (requestCode == PERMISSION_MULTIPLE) {
            for (int grantedResult : grantResults) {
                if(grantedResult != PackageManager.PERMISSION_GRANTED)
                {
                    allGranted = false;
                }
            }
            if(allGranted)
            {
                startCamera(); //two startcamera calls need to refactor
            }
        }
        /*if(requestCode == PERMISSION_REQUEST_CAMERA)
        {
            //check that the camera request is PERMISSION GRANTED
            if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) //refactor code later
            {

                startCamera();
            }
            else {
                // indicate to user that permission has not been granted from request
            }
        }*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) { //callback
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK && requestCode == PERMISSION_REQUEST_TAKE_PHOTO){
            try
            {
                image = BitmapFactory.decodeFile(currentPhotoPath);
                //image = processImage(image, 0.5f, 0);
//                urpic.setImageBitmap(image);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            /*note
            *   full-size image
            * */
        }
    }


    private static Bitmap processImage(Bitmap imageToProcess, float contrast, float brightness)
    {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });
        Bitmap ret = Bitmap.createBitmap(imageToProcess.getWidth(), imageToProcess.getHeight(), imageToProcess.getConfig());
        Canvas canvas = new Canvas(ret);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(imageToProcess, 0, 0, paint);
        return ret;
    }

    private void startCamera() //start capture image and create image file
    {
        Intent capturePhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        /*List<ResolveInfo> resInfoList =  getPackageManager().queryIntentActivities(capturePhoto, PackageManager.MATCH_DEFAULT_ONLY);
        for(ResolveInfo resolveInfo : resInfoList)
        {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName,);
        }*/
        if(capturePhoto.resolveActivity(getPackageManager()) == null)
        {
            // indicate to user there is no app that has camera functionality
        }
        else{
            File imageFile = null;
            try
            {

                imageFile = createImageFile();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            if (imageFile != null)
            {

                imageFileUri = FileProvider.getUriForFile(this, "com.team6capstone.textrecognizer.fileprovider", imageFile);
                capturePhoto.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
                startActivityForResult(capturePhoto, PERMISSION_REQUEST_TAKE_PHOTO);
            }
        }
    }

    private void runTextRecognition(final Bitmap receipt)
    {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(receipt);
        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        convert.setEnabled(false);
        detector.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        convert.setEnabled(true);
                        List<String> allWords = getAllWordsRecognized(firebaseVisionText);
                        Date receiptDate = getReceiptDate(allWords);
                        //SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
                        //String strDate = formatter.format(receiptDate);
                        //Log.d("receiptDate is", strDate);
                        List<String> listOfFruitVegetables = getFruitsVegetablesWords(allWords);
                        addItemsToList(receiptDate, listOfFruitVegetables);
                        for(dataClass item : itemList)
                        {
                            Log.d("item is", item.getItemName());
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        convert.setEnabled(true);
                        e.printStackTrace();
                    }
                });
    }

    private Date getReceiptDate(List<String> words)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
        Date receiptDate = null;
        for(String word : words)
        {
            try{
                receiptDate = dateFormat.parse(word);
                break;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
                continue;
            }
        }
        return receiptDate;
    }

    private void addItemsToList(Date receiptDate, List<String> listOfFruitVegetables)
    {
        if(receiptDate == null)
            receiptDate = new Date();
        for(String itemName : listOfFruitVegetables) {
            dataClass scannedItem = new dataClass();
            scannedItem.setItemDate(receiptDate);
            scannedItem.setItemName(itemName);
            itemList.add(scannedItem);
        }
    }

    private List<String> getFruitsVegetablesWords(List<String> allWords)
    {
        List<String> fruitAndVegetableNames = new ArrayList<>();
        for(String word: allWords)
        {
            Log.d("word is",word);
            for(String itemName : itemNames)
            {
                if(English.plural(itemName).equals(word) || itemName.equals(word))
                {
                    fruitAndVegetableNames.add(itemName);
                    break;
                }
            }
        }
        return fruitAndVegetableNames;
    }

    private List<String> getAllWordsRecognized(FirebaseVisionText texts)
    {
        //get the whole string recognized
        //get all elements/words

        String wholestring = texts.getText();
        List<String> returnWords =  new ArrayList<>();

        Log.d("test convert image-text",wholestring);
        if (wholestring == null)
        {
            convertedText.setText("cannot convert image to text :(");
        }
        convertedText.setText(wholestring);
        /*
        if u want u can further parse whole string to extract key components using
            ->  FirebaseVisionText.Element
            ->  FirebaseVisionText.Line
            ->  FirebaseVisionText.Textblock
        */
        if(image != null) {
            //Paint rectPaint = new Paint();
            //rectPaint.setColor(Color.YELLOW);
            //rectPaint.setStyle(Paint.Style.STROKE);
            //rectPaint.setStrokeWidth(4.0f);

            //Bitmap tempBitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.RGB_565);
            //imageCanvas = new Canvas(tempBitmap);
            //imageCanvas.drawBitmap(image, 0, 0 ,null);
            //add bounding boxes to better visualize which words have been recognized
            for (FirebaseVisionText.TextBlock block : texts.getTextBlocks()) {
                //go through each block
                for (FirebaseVisionText.Line line : block.getLines()) {
                    //go through each line for block
                    for (FirebaseVisionText.Element element : line.getElements()) {
                        //go through each element/word for line

                        returnWords.add(element.getText().toLowerCase());
                        Log.d("recog word", element.getText().toLowerCase());
                        //draw onto canvas
                        //Rect elementFrame = element.getBoundingBox();
                        //imageCanvas.drawRect(elementFrame, rectPaint);
                        //urpic.setImageDrawable(new BitmapDrawable(this.getResources(),tempBitmap));
                    }
                }
            } //use this for getting elements and bounding boxes around recognized texts
        }
        return returnWords;
    }

    private File createImageFile() throws IOException
    {
        String imagetimestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "receipt_" + imagetimestamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image =  File.createTempFile(imageFileName, ".jpg", storageDir); //return the file created
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //store all item names in lowercase and their links to dataclass obj itemlink field
    private int getItemLinks(int timeOutSec,  String... urls)
    {
        Document doc;
        Elements elements;
        int numberofLinksAdded = 0;
        //connect to the website
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
                            //items.put(eatbyDateItemName, element.attr("href"));
                            //Log.d("heading-link",element.attr("href"));
                            //Log.d("name of item" ,element.text().toLowerCase());
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

    //get the duration dates from a hashmap of key: itemname value: iteminfolink. Returns the duration of the item
    private void getItemDurations(int numberofLinksToSearch, int timeOutSec)
    {
        Document doc;
        if(numberofLinksToSearch == 0)
        {
            Log.d("0Links", "no links provided to search");
            return;
        }

        /*for(dataClass item: itemList)
        {
            Log.d("DEBUG ITEMNAME", item.getItemName());
        }*/
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
                        //Log.d("entry value", cells.get(indexOfTargetCol).text());
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
            item.setExpiryDate(storingDuration);
            Log.d("setItemExpiryDate", item.getItemExpiryDate().toString());
            item.setExpiryDuration(days_difference);
            Log.d("setItemExpiry", String.valueOf(item.getItemExpiryDuration()));
        }


    }
    //do web scraping asynchronously
    private class webData extends AsyncTask<Void, Void, Void> {
        public ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //show loading progress
            progress = new ProgressDialog(activity_third.this);
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
            pullData.setEnabled(true);
            //Log.d("SCRAP RESULTS",document.outerHtml()); //display on logcat for now.
        }

    }

    /*private void checkExternalStorageReadWrite(boolean read_write, int request)
    {
        String rw;
        if (read_write)
        {
            rw = Manifest.permission.WRITE_EXTERNAL_STORAGE;
        }
        else{
            rw = Manifest.permission.READ_EXTERNAL_STORAGE;
        }
        int storageAccessPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if(storageAccessPermission!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{rw}, request);
        }

    }*/

}

/* image-to-text challenges
*
* Challenges:
*
*  - break receipt line by line?
*  i.e
*       Cleaning supplies // these words will also show up in list but user need to discard this (not item)
*            towel (item)
*            detergent (item)
*       Groceries //  same for this
*            green apple (item)
*            red apple (item)
*            CompanyX apple (item) //different apples -> list includes different apples as well?
*
*  - dense receipt each line very close to each other
*       -Take different sections but with no overlap? (an user constraint)
*       -Recognize overlapped sections? (may be difficult to achieve)
* */
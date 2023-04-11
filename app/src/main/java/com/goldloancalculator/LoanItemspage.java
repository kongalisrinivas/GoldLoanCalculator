package com.goldloancalculator;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.goldloancalculator.interfaces.OnLoanItemClick;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.type.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;


public class LoanItemspage extends AppCompatActivity implements OnLoanItemClick {
    // Request code for creating a PDF document.
    private static final int CREATE_FILE = 1;

    Button itemAddbtn, confirmbtn, createPDFBtn;
    RecyclerView rec_itemsList;
    ItemsAdapter itemsAdapter;
    TextInputLayout customerName, mobileNumber;
    ProgressBar progressBar;
    TextView TotalbankAmount, TotalappraisalAmount, ItemCount, Itemsadded, Selectbank, apraisedAmount, bankgivenAmount, totalNetWeight, totalGrossWeight;
    boolean AllFieldschecked = false;
    boolean isSpinnerSelect;
    Toolbar mtoolbar;
    String itemName, grossWeight, netWeight, bankValue_det, Karats_det, custName, mobNumber, bankName, totalItems;
    double gross, bank, appraisal, bankAmount, appraisalAmount, karat, BankTtl, AppraisalTtl, TotalGrossWeight, TotalNetWeight, net;
    int bankgivenamount, apraiseddAmount;
    ArrayList<Item_Model> finalItemList;
    ArrayList<SingleItem> singleItems;
    DatabaseReference referenceRecord;

    Details details;
    int recordCount = 1;
    String isView;
    DatabaseReference reference;
    String UID;
    Record_Model model;
    PdfDocument pdfDocument;
    //Button generatePDF;
    // declaring width and height
    // for our PDF file.
    int pageHeight = 1120;
    int pagewidth = 792;
    int left = 20;
    int top = 20;

    // creating a bitmap variable
    // for storing our images
    Bitmap bmp, scaledbmp;

    // constant code for runtime permissions
    private static final int PERMISSION_REQUEST_CODE = 200;
    ActivityResultLauncher<Intent> launchActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loans_itemspage);

        Bundle extras = getIntent().getExtras();

        findViewByIDs();

        model = (Record_Model) extras.getParcelable("model");

        finalItemList = new ArrayList<>();
        singleItems = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Users");
        UID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (model != null) {
            setData();
            itemAddbtn.setVisibility(View.GONE);
            confirmbtn.setVisibility(View.GONE);
            createPDFBtn.setVisibility(View.VISIBLE);
            customerName.setEnabled(false);
            mobileNumber.setEnabled(false);
            Selectbank.setClickable(false);
        } else {
            itemAddbtn.setVisibility(View.VISIBLE);
            confirmbtn.setVisibility(View.VISIBLE);
            createPDFBtn.setVisibility(View.GONE);
            customerName.setEnabled(true);
            mobileNumber.setEnabled(true);
            Selectbank.setClickable(true);
        }

        Selectbank.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model == null) {
                    Intent addBankIntent = new Intent(LoanItemspage.this, Addbank.class);
                    addBankIntent.putExtra("role", "");
                    startActivityForResult(addBankIntent, 2);
                }
            }
        });

        itemAddbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                custName = customerName.getEditText().getText().toString();
                mobNumber = mobileNumber.getEditText().getText().toString();

                if (isSpinnerSelected()) {
                    showAddItemDialog("", "", "", "", "" + apraiseddAmount, "" + bankgivenamount, true, 0);
                } else {
                    isSpinnerSelected();
                }
            }
        });

        confirmbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (finalItemList.size() != 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    Record_Model recordDetails = new Record_Model();

                    recordDetails.setTotalApprisalAmount(String.valueOf(AppraisalTtl));
                    recordDetails.setTotalBankGivenAmount(String.valueOf(BankTtl));
                    recordDetails.setTotalGrossWeight(String.valueOf(TotalGrossWeight));
                    recordDetails.setTotalNetWeight(String.valueOf(TotalNetWeight));

                    recordDetails.setBankName(bankName);
                    recordDetails.setCustomerName(custName);
                    recordDetails.setMobileNumber(mobNumber);
                    recordDetails.setItemCount(totalItems);
                    recordDetails.setTitle("Record");
                    recordDetails.setItem_model(finalItemList);
                    recordDetails.setCreatedDate(getDate());
                    recordDetails.setRecordCount(String.valueOf(finalItemList.size()));


                    reference.child(UID).child("Records").push().setValue(recordDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                recordCount++;
                                progressBar.setVisibility(View.GONE);
                                finish();
                                Toast.makeText(LoanItemspage.this, "Data Saved", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(LoanItemspage.this, "Something wrong, Try again later.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        // Finish Activity when click on Back Arrow;
        mtoolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        createPDFBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                bmp = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
                scaledbmp = Bitmap.createScaledBitmap(bmp, 100, 100, false);

                // below code is used for
                // checking our permissions.
                //if (checkPermission()) {
                    //Toast.makeText(LoanItemspage.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    //generatePDF();
                    createFile(model.customerName.trim()+".pdf");
                //} else {
                  //  requestPermission();
                //}
            }
        });

//    openPDF.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                try {
//                File file = new File("/sdcard/Appraiser Calculator/"+ model.customerName+".pdf");
//                    Uri photoURI = FileProvider.getUriForFile(LoanItemspage.this, LoanItemspage.this.getApplicationContext().getPackageName() + ".provider", file);
//                Intent target = new Intent(Intent.ACTION_VIEW);
//                target.setDataAndType(photoURI,"application/pdf");
//                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//                Intent intent = Intent.createChooser(target, "Open File");
//
//                    startActivity(intent);
//                } catch (Exception e) {
//                    // Instruct the user to install a PDF reader here, or something
//                    e.printStackTrace();
//                }
//            }
//        });

        // Create launcher variable inside onAttach or onCreate or global
  launchActivity = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent data = result.getData();
                            Uri uri = data.getData();
                            // your operation....
                            generatePDF(uri);
                        }
                    }
                });

    }

    private void createFile(String fileName) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);

        // Optionally, specify a URI for the directory that should be opened in
        // the system file picker when your app creates the document.
        intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, CREATE_FILE);
        launchActivity.launch(intent);
        //startActivityForResult(intent, CREATE_FILE);


    }
    private void generatePDF(Uri uri) {
        // creating an object variable
        // for our PDF document.
         pdfDocument = new PdfDocument();

        // two variables for paint "paint" is used
        // for drawing shapes and we will use "title"
        // for adding text in our PDF file.
        Paint paint = new Paint();
        Paint title = new Paint();
        Paint titleHeads = new Paint();
        Paint divider = new Paint();

        // we are adding page info to our PDF file
        // in which we will be passing our pageWidth,
        // pageHeight and number of pages and after that
        // we are calling it to create our PDF.
        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();

        // below line is used for setting
        // start page for our PDF file.
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        // creating a variable for canvas
        // from our page of PDF.
        Canvas canvas = myPage.getCanvas();

        // below line is used to draw our image on our PDF file.
        // the first parameter of our drawbitmap method is
        // our bitmap
        // second parameter is position from left
        // third parameter is position from top and last
        // one is our variable for paint.
        canvas.drawBitmap(scaledbmp, left*4, top*3-10, paint);

        // below line is used for adding typeface for
        // our text which we will be adding in our PDF file.
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        titleHeads.setTypeface(Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD));

        // below line is used for setting text size
        // which we will be displaying in our PDF file.
        title.setTextSize(22);
        titleHeads.setTextSize(32);

        // below line is sued for setting color
        // of our text inside our PDF file.
        title.setColor(ContextCompat.getColor(this, R.color.black));
        titleHeads.setColor(ContextCompat.getColor(this, R.color.black));

        // below line is used to draw text in our PDF file.
        // the first parameter is our text, second parameter
        // is position from start, third parameter is position from top
        // and then we are passing our variable of paint which is title.
        canvas.drawText("Appraiser Calculator", left*2, top*10, titleHeads);

        // below line is used to draw line in our PDF file.
        divider.setStyle(Paint.Style.STROKE);
        divider.setPathEffect(new DashPathEffect(new float[]{5,5},0));
        divider.setStrokeWidth(1);
        canvas.drawLine(20,220,pagewidth-20,220, divider);


        // similarly we are creating another text and in this
        // we are aligning this text to center of our PDF file.
        canvas.drawText("Bank Name : " + model.getBankName(), left*2, top*13, title);
        canvas.drawText("Date : " + model.getCreatedDate(), left*28, top*13, title);
        canvas.drawText("Customer Name : "+ model.getCustomerName(), left*2, top*15, title);
        canvas.drawText("Customer Mobile Number : "+ model.getMobileNumber(), left*2, top*17, title);
        canvas.drawText("Loan Account Number :", left*2, top*19, title);

        canvas.drawLine(left*2,top*20,pagewidth-20,top*20, divider);

        title.setTextSize(16);
        canvas.drawText("Sl.No", left*2, top*22, title);
        canvas.drawText("Item Name", left*5, top*22, title);
        canvas.drawText("Gross Wt", left*15, top*22, title);
        canvas.drawText("Net Wt", left*21, top*22, title);
        canvas.drawText("Appraiser Value", left*26, top*22, title);
        canvas.drawText("Bank Value", left*33, top*22, title);

        canvas.drawLine(left*2,top*23,pagewidth-20,top*23, divider);

        for(int i=0; i<model.item_model.size();i++) {
            canvas.drawText(String.valueOf(i+1), left * 2, (25+i)*top , title);
            canvas.drawText(model.item_model.get(i).getItemName(), left * 5, (25+i)*top, title);
            canvas.drawText(String.format("%.1f", Double.parseDouble(model.item_model.get(i).getGrossWeight())), left * 15, (25+i)*top, title);
            canvas.drawText(String.format("%.1f", Double.parseDouble(model.item_model.get(i).getNetWeight())), left * 21, (25+i)*top, title);
            canvas.drawText(String.format("%.1f", Double.parseDouble(model.item_model.get(i).getApraisalValue())), left * 26, (25+i)*top, title);
            canvas.drawText(String.format("%.1f", Double.parseDouble(model.item_model.get(i).getBankGivenAmount())), left * 33, (25+i)*top, title);
        }

        canvas.drawLine(left*2,(27+model.item_model.size())*top,pagewidth-20,(27+model.item_model.size())*top, divider);
        title.setTextAlign(Paint.Align.LEFT);
        title.setTextSize(16);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        canvas.drawText("Grand Total", left * 5, (29+model.item_model.size())*top, title);

        DecimalFormat formatter = new DecimalFormat("#,###,###.#");
        String yourFormattedString = formatter.format(100000);

        canvas.drawText(String.format("%.1f", Double.parseDouble(model.getTotalGrossWeight())), left * 15, (29+model.item_model.size())*top, title);
        canvas.drawText(String.format("%.1f", Double.parseDouble(model.getTotalNetWeight())), left * 21, (29+model.item_model.size())*top, title);
        canvas.drawText(NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(model.getTotalApprisalAmount())), left * 26, (29+model.item_model.size())*top, title);
        canvas.drawText(NumberFormat.getNumberInstance(Locale.US).format(Double.parseDouble(model.getTotalBankGivenAmount())), left * 33, (29+model.item_model.size())*top, title);
        // after adding all attributes to our
        // PDF file we will be finishing our page.
        canvas.drawText("Signature", left * 2, (35+model.item_model.size())*top, title);
        canvas.drawText("Signature", left * 15, (35+model.item_model.size())*top, title);
        canvas.drawText("Signature", left * 30, (35+model.item_model.size())*top, title);

        canvas.drawText("Borrower", left * 2, (38+model.item_model.size())*top, title);
        canvas.drawText("Appraiser", left * 15, (38+model.item_model.size())*top, title);
        canvas.drawText("Manager/ Officer", left * 30, (38+model.item_model.size())*top, title);
        pdfDocument.finishPage(myPage);

            // below line is used to set the name of
        // our PDF file and its path.
        //File file = new File(Environment.getExternalStorageDirectory(), "/Appraiser Calculator/Load1.pdf");
        // create a File object for the parent directory
        //File appraiser = new File("/sdcard/Appraiser Calculator/");

        //..................................................

        //        // have the object build the directory structure, if needed.
        //        //appraiser.mkdirs();
        //        File appraiser = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/Appraiser Calculator/");
        //        appraiser.mkdirs();
        //        // create a File object for the output file
        //        File file = new File(appraiser, model.customerName+".pdf");
        //
        //        try {
        //            // after creating a file name we will
        //            // write our PDF file to that location.
        //            pdfDocument.writeTo(new FileOutputStream(file));
        //
        //            //Toast.makeText(LoanItemspage.this, "File downloaded successfully.", Toast.LENGTH_SHORT).show();
        //
        //        } catch (IOException e) {
        //            // below line is used
        //            // to handle error
        //            e.printStackTrace();
        //            Toast.makeText(LoanItemspage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        //
        //        }
        //        // after storing our pdf to that
        //        // location we are closing our PDF file.
        //        pdfDocument.close();
        //..................................................
        // Open PDF
        //openDownloadedPDF(file);
        alterDocument(uri);

    }
    private void alterDocument(Uri uri) {
        try {
            ParcelFileDescriptor pfd = LoanItemspage.this.getContentResolver().
                    openFileDescriptor(uri, "w");
            FileOutputStream fileOutputStream =
                    new FileOutputStream(pfd.getFileDescriptor());
            // Let the document provider know you're done by closing the stream.
            pdfDocument.writeTo(fileOutputStream);
            fileOutputStream.close();
            pfd.close();
            pdfDocument.close();
            progressBar.setVisibility(View.GONE);
            openDownloadedPDF(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void openDownloadedPDF(Uri file) {
        try {
            //File file = new File("/sdcard/Appraiser Calculator/"+ model.customerName+".pdf");
            //Uri photoURI = FileProvider.getUriForFile(LoanItemspage.this, LoanItemspage.this.getApplicationContext().getPackageName() + ".provider", file);
            Intent target = new Intent(Intent.ACTION_VIEW);
            target.setDataAndType(file,"application/pdf");
            target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            Intent intent = Intent.createChooser(target, "Open File");

            startActivity(intent);
        } catch (Exception e) {
            // Instruct the user to install a PDF reader here, or something
            e.printStackTrace();
            Toast.makeText(LoanItemspage.this, "Something went wrong while downloading the file. Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    //Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                    //generatePDF();
                    createFile(model.customerName+".pdf");
                } else {
                    Toast.makeText(this, "Permission Denied. Please give permission and try again", Toast.LENGTH_SHORT).show();
                    //finish();
                }
            }
        }
    }
    private String getDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM/yyyy");
        Date date = new Date();
        //System.out.println(formatter.format(date);
        return formatter.format(date);
    }

    private void findViewByIDs() {
        rec_itemsList = findViewById(R.id.rec_Itemlist);
        customerName = findViewById(R.id.name);
        mobileNumber = findViewById(R.id.mobile);
        Itemsadded = findViewById(R.id.NoitemsAdded);
        Selectbank = findViewById(R.id.txt_selectBank);
        confirmbtn = findViewById(R.id.confirm_btn);
        TotalbankAmount = findViewById(R.id.totalBankAmount);
        TotalappraisalAmount = findViewById(R.id.totalAppraisalAmount);
        ItemCount = findViewById(R.id.ItemCount);
        itemAddbtn = findViewById(R.id.addItem_btn);
        totalNetWeight = findViewById(R.id.netWeight);
        totalGrossWeight = findViewById(R.id.grossWeight);
        mtoolbar = findViewById(R.id.toolbar);
        createPDFBtn = findViewById(R.id.createPDFBtn);
        progressBar = findViewById(R.id.progressBar);
        //openPDF = findViewById(R.id.openPDF);

    }

    private void setData() {

        Selectbank.setText(model.bankName);
        customerName.getEditText().setText(model.customerName);
        mobileNumber.getEditText().setText(model.mobileNumber);
        ItemCount.setText(model.itemCount);

        TotalappraisalAmount.setText(" \u20B9 " + String.format("%.1f", Double.parseDouble(model.totalApprisalAmount)));
        TotalbankAmount.setText(" \u20B9 " + String.format("%.1f", Double.parseDouble(model.totalBankGivenAmount)));
        totalGrossWeight.setText(String.format("%.1f", Double.parseDouble(model.totalGrossWeight)));
        totalNetWeight.setText(String.format("%.1f", Double.parseDouble(model.totalNetWeight)));

        finalItemList.clear();

        for (Item_Model childModel : model.item_model) {
            if(childModel !=null) {
                String itemName = childModel.itemName;
                String appraisalValue = childModel.getApraisalValue();
                String bankGivenAmount = childModel.bankGivenAmount;
                String grossWeight = childModel.grossWeight;
                String netWeight = childModel.netWeight;
                String Karats = childModel.Karats;
                finalItemList.add(new Item_Model(itemName, grossWeight, netWeight, appraisalValue, bankGivenAmount, Karats));
            }
            }
        if (finalItemList.size() > 0) {
            rec_itemsList.setVisibility(View.VISIBLE);
            Itemsadded.setVisibility(View.GONE);
        }

        itemsAdapter = new ItemsAdapter(finalItemList, false);
        itemsAdapter.setClickListner(LoanItemspage.this);
        rec_itemsList.setHasFixedSize(false);
        rec_itemsList.setNestedScrollingEnabled(false);
        rec_itemsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rec_itemsList.setAdapter(itemsAdapter);
    }

    private void showAddItemDialog(String ItemName, String GrossWeight, String Karats, String Netweight, String apraiseddAmount, String bankgivenamount, boolean isNewRecord, int pos) {

        final AlertDialog.Builder dialog = new AlertDialog.Builder(LoanItemspage.this);
        View mView = getLayoutInflater().inflate(R.layout.layout_dialog, null);

        Button saveBtn = mView.findViewById(R.id.saveBtn);
        Button closeBtn = mView.findViewById(R.id.closeBtn);
        EditText edt_ItemName = mView.findViewById(R.id.edt_Itemname);
        EditText edt_grossWeight = mView.findViewById(R.id.edt_Grossweight);
        Spinner sp_Karat = mView.findViewById(R.id.sp_karats);
        TextView NetWeight = mView.findViewById(R.id.netWeightAmount);

        apraisedAmount = mView.findViewById(R.id.appraisalAmount_txt);
        bankgivenAmount = mView.findViewById(R.id.BAmount_txt);

        bankgivenAmount.setText(DisplayIndianCurrency(Double.parseDouble(String.valueOf(bankgivenamount))));
        apraisedAmount.setText(DisplayIndianCurrency(Double.parseDouble(String.valueOf(apraiseddAmount))));
        edt_ItemName.setText(ItemName);
        edt_grossWeight.setText(GrossWeight);
        NetWeight.setText(Netweight);
        String[] stringArray = getResources().getStringArray(R.array.Karats);
        if(!Karats.isEmpty()) {
            for (int i = 0; i < stringArray.length; i++) {
                if (stringArray[i].contains(Karats)) {
                    sp_Karat.setSelection(i);
                }
            }
        }
        if (model != null) {
            saveBtn.setVisibility(View.GONE);
            edt_ItemName.setEnabled(false);
            edt_grossWeight.setEnabled(false);
            sp_Karat.setEnabled(false);

        } else {
            saveBtn.setVisibility(View.VISIBLE);
            edt_ItemName.setEnabled(true);
            edt_grossWeight.setEnabled(true);
            sp_Karat.setEnabled(true);
        }
        sp_Karat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0 && !edt_grossWeight.getText().toString().isEmpty()) {
                    grossWeight = edt_grossWeight.getText().toString();
                    Karats_det = parent.getItemAtPosition(position).toString().substring(0, 2);
                    karat = Integer.parseInt(Karats_det);
                    gross = Float.parseFloat(grossWeight);
                    net = (float) ((gross * karat / 100));
                    NetWeight.setText(String.format("%.1f", net));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
//        edt_Karat.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (s.length() > 1) {
//                    grossWeight = edt_grossWeight.getText().toString();
//                    Karats_det = edt_Karat.getText().toString();
//                    karat = Integer.parseInt(Karats_det);
//                    gross = Float.parseFloat(grossWeight);
//                    net = (float) ((gross * karat / 100));
//                    NetWeight.setText(String.format("%.1f", net));
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//            }
//        });


        dialog.setView(mView);


        final AlertDialog alertDialog = dialog.create();
        alertDialog.setCanceledOnTouchOutside(false);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                itemName = edt_ItemName.getText().toString();
                netWeight = NetWeight.getText().toString();
                bankAmount = net * Double.parseDouble(bankgivenamount);
                appraisalAmount = net * Double.parseDouble(apraiseddAmount);

                /*BankTtl = BankTtl + bankAmount;
                AppraisalTtl = AppraisalTtl + appraisalAmount;
                TotalGrossWeight = TotalGrossWeight + gross;
                TotalNetWeight = TotalNetWeight + net;*/
                Item_Model item = new Item_Model(itemName, grossWeight, netWeight, "" + appraisalAmount, "" + bankAmount, Karats_det);

                if (isNewRecord) {
                    finalItemList.add(item);
                } else {
                    finalItemList.set(pos, item);
                }

                if (finalItemList.size() > 0) {
                    rec_itemsList.setVisibility(View.VISIBLE);
                    Itemsadded.setVisibility(View.GONE);
                    confirmbtn.setVisibility(View.VISIBLE);
                }

                itemsAdapter = new ItemsAdapter(finalItemList, true);
                rec_itemsList.setHasFixedSize(false);
                itemsAdapter.setClickListner(LoanItemspage.this);
                rec_itemsList.setNestedScrollingEnabled(false);
                rec_itemsList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                rec_itemsList.setAdapter(itemsAdapter);

                // Update grand totals
                updateTotals(finalItemList);

                if (isAllFieldsChecked()) {
                    alertDialog.dismiss();
                }
            }


            private boolean isAllFieldsChecked() {
                if (edt_ItemName.length() == 0) {
                    edt_ItemName.setError("This field is required");
                    return false;
                }

                if (edt_grossWeight.length() == 0) {
                    edt_grossWeight.setError("This field is required");
                    return false;
                }

                if (sp_Karat.getSelectedItemPosition() == 0) {
                    ((TextView) sp_Karat.getSelectedView()).setError("his field is required");
                    return false;
                }
                return true;
            }
        });

        alertDialog.show();

    }

    private void updateTotals(ArrayList<Item_Model> finalItemList) {
        BankTtl = 0;
        AppraisalTtl = 0;
        TotalGrossWeight = 0;
        TotalNetWeight = 0;

        for (Item_Model item : finalItemList) {
            BankTtl = BankTtl + Double.parseDouble(item.bankGivenAmount);
            AppraisalTtl = AppraisalTtl + Double.parseDouble(item.ApraisalValue);
            TotalGrossWeight = TotalGrossWeight + Double.parseDouble(item.grossWeight);
            TotalNetWeight = TotalNetWeight + Double.parseDouble(item.netWeight);
        }

        totalItems = String.valueOf(finalItemList.size());
        ItemCount.setText(String.valueOf(finalItemList.size()));
        totalNetWeight.setText(String.format("%.1f", TotalNetWeight) + " grm");
        totalGrossWeight.setText(TotalGrossWeight + " grm");
        TotalbankAmount.setText(" \u20B9 " + DisplayIndianCurrency(Double.parseDouble(String.format("%.1f", BankTtl))));
        TotalappraisalAmount.setText(" \u20B9 " + DisplayIndianCurrency(Double.parseDouble(String.format("%.1f", AppraisalTtl))));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            if (data != null) {
                bankName = data.getStringExtra("Bankname");
                apraiseddAmount = data.getIntExtra("Apraisedamount", 0);
                bankgivenamount = data.getIntExtra("Bankgivenamount", 0);
                Selectbank.setText(bankName);
            }
        }
    }

    private String DisplayIndianCurrency(double amount) {
        DecimalFormat IndianCurrencyFormat = new DecimalFormat("##,##,###.##");
        return IndianCurrencyFormat.format(amount);
    }

    private boolean isSpinnerSelected() {
        if (bankName == null) {
            Toast.makeText(getApplicationContext(), "Select your bank", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (custName.length() == 0) {
            customerName.setError("This field is required");
            return false;
        }

        if (mobNumber.length() == 0) {
            mobileNumber.setError("This field is required");
            return false;
        }
        return true;
    }

    @Override
    public void onItemClick(Item_Model data, int pos) {
        showAddItemDialog(data.itemName, data.grossWeight, data.Karats, data.netWeight, data.ApraisalValue, data.bankGivenAmount, false, pos);
    }

    @Override
    public void onItemDeleteClick(int pos) {
        finalItemList.remove(pos);
        itemsAdapter.notifyDataSetChanged();
        updateTotals(finalItemList);
    }
}
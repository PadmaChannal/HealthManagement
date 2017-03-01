package com.along.android.healthmanagement.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.along.android.healthmanagement.R;
import com.along.android.healthmanagement.adapters.MedicationDetailAdapter;
import com.along.android.healthmanagement.entities.Medicine;
import com.along.android.healthmanagement.entities.Prescription;
import com.along.android.healthmanagement.helpers.EntityManager;

import java.util.List;

public class MedicationDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medication_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String selectedPrescriptionItemId = getIntent().getStringExtra("selectedPrescriptionItemId");
        // got the prescription
        Prescription prescription = EntityManager.findById(Prescription.class, Long.parseLong(selectedPrescriptionItemId));

        TextView patientNameTV = (TextView) findViewById(R.id.xlMDPatientName);
        TextView doctorNameTV = (TextView) findViewById(R.id.xlMDDoctorName);
        TextView diseaseTV = (TextView) findViewById(R.id.xlMDDisease);
        TextView prescriptionDateTV = (TextView) findViewById(R.id.xlMDPrescriptionDate);

        // Patient Name:
        String patientText = "Patient name: " + (null != prescription.getPatientName() ? prescription.getPatientName() : "");
        patientNameTV.setText(patientText);

        // Doctor Name:
        String doctorText = "Prescribed by: " + (null != prescription.getDoctorName() ? prescription.getDoctorName() : "");
        doctorNameTV.setText(doctorText);

        // Disease:
        String diseaseText = "For " + (null != prescription.getDisease() ? prescription.getDisease() : "");
        diseaseTV.setText(diseaseText);

        // Prescription Date
        String prescriptionDateText = "Prescription date: " + (null != prescription.getDisease() ? prescription.getStartDate() : "");
        prescriptionDateTV.setText(prescriptionDateText);

//        System.out.println("----->>>>>>>>>>>>");
//        System.out.println(prescription.getMedication());

        // prescriptionId
        List<Medicine> medicineList = EntityManager.find(Medicine.class, "pid = ?", prescription.getId() + "");

        MedicationDetailAdapter medicationDetailAdapter =
                new MedicationDetailAdapter(this, medicineList);
        ListView listView = (ListView) findViewById(R.id.medication_detail_list);
        listView.setAdapter(medicationDetailAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

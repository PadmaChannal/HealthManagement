package com.along.android.healthmanagement.fragments;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.along.android.healthmanagement.R;
import com.along.android.healthmanagement.adapters.AutoSuggestedFoodAdapter;
import com.along.android.healthmanagement.adapters.FoodAddedToMealAdapter;
import com.along.android.healthmanagement.entities.Food;
import com.along.android.healthmanagement.helpers.Utility;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddMealFragment extends BasicFragment {

    private static final int THRESHOLD = 3;
    private static final String APP_ID = "08310541";
    private static final String APP_KEY = "b6d4734ba1b80bce25497956cfb60ca9";
    private static final String URL = "https://api.nutritionix.com/v1_1/search/";
    private static final String UPC_URL = "https://api.nutritionix.com/v1_1/item/";
    private static final String REQUIRED_FIELDS_IN_RESPONSE = "item_name%2Citem_id%2Cnf_calories%2Cnf_serving_size_qty%2Cnf_serving_size_unit"; //%2C means comma


    private AutoSuggestedFoodAdapter autoSuggestedFoodAdapter;
    private FoodAddedToMealAdapter foodAddedToMealAdapter;
    private ProgressDialog prgDialog;
    private ListView autoSuggestFoodListView;
    private ListView addedFoodListView;
    private LinearLayout llAddFoodSection;
    private LinearLayout llBarcodeSection;
    private LinearLayout llSearchResultSection;

    private List<Food> autoSuggestedFoods;

    public AddMealFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_meal, container, false);

        initLinearLayouts(view);
        initTitle(view);
        initSearch(view);
        initBarcode(view);
        initAddedFoodList(view);
        initSearchResultList(view);
        return view;
    }

    private void initLinearLayouts(View view) {
        llAddFoodSection = (LinearLayout) view.findViewById(R.id.ll_added_food_section);
        llSearchResultSection = (LinearLayout) view.findViewById(R.id.ll_search_result_section);
        llBarcodeSection = (LinearLayout) view.findViewById(R.id.ll_barcode_section);
    }

    private void initAddedFoodList(View view) {
        foodAddedToMealAdapter = new FoodAddedToMealAdapter(getContext(), new ArrayList<Food>());
        addedFoodListView = (ListView) view.findViewById(R.id.added_food_list);
        addedFoodListView.setAdapter(foodAddedToMealAdapter);

        addedFoodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    public void addFoodToMeal(Food food) {
        // hide keyboard
        // Hide search view and show food added to meal view
        // Populate the list view with added food
        Utility.hideSoftKeyboard(getActivity());
        llAddFoodSection.setVisibility(View.VISIBLE);
        llSearchResultSection.setVisibility(View.GONE);
        foodAddedToMealAdapter.add(food);
    }

    private void initBarcode(View view) {
        LinearLayout llBarcodeSection = (LinearLayout) view.findViewById(R.id.ll_barcode_section);
        llBarcodeSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFragment(new BarcodeScannerFragment(), "cameraPreviewFragment");
            }
        });
    }

    private void initTitle(View view) {
        String mealType = getArguments().getString("mealType");
        String mealDate = getArguments().getString("mealDate");
        TextView tvMealName = (TextView) view.findViewById(R.id.tv_meal_name);
        tvMealName.setText(mealType);
        TextView tvMealDate = (TextView) view.findViewById(R.id.tv_meal_date);
        tvMealDate.setText(mealDate);
    }

    private void initSearchResultList(View view) {
        TextView tvEmptyMsg = (TextView) view.findViewById(R.id.tv_no_food_entered_msg);

        autoSuggestedFoodAdapter = new AutoSuggestedFoodAdapter(getActivity(), new ArrayList<Food>(), AddMealFragment.this);
        autoSuggestFoodListView = (ListView) view.findViewById(R.id.auto_suggest_food_list);

        autoSuggestFoodListView.setEmptyView(tvEmptyMsg);
        autoSuggestFoodListView.setAdapter(autoSuggestedFoodAdapter);

        autoSuggestFoodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });
    }

    private void initSearch(View view) {
        prgDialog = new ProgressDialog(getActivity());
        prgDialog.setMessage("Please wait...");
        prgDialog.setCancelable(true);

        SearchView svSearchFood = (SearchView) view.findViewById(R.id.sv_search_food);
        svSearchFood.setIconifiedByDefault(false);
        svSearchFood.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= THRESHOLD) {
                    // hide barcode section and show search result section
                    llBarcodeSection.setVisibility(View.GONE);
                    llSearchResultSection.setVisibility(View.VISIBLE);

                    getAutoSuggestFoodNames(newText.toString());
                } else {
                    // hide search result section and show barcode section
                    llBarcodeSection.setVisibility(View.VISIBLE);
                    llSearchResultSection.setVisibility(View.GONE);
                }
                return false;
            }
        });
    }

    private void getAutoSuggestFoodNames(String typedFoodName) {
        prgDialog.show();
        autoSuggestedFoods = new ArrayList<Food>();

        AsyncHttpClient client = new AsyncHttpClient();

        String request = URL + typedFoodName + "?fields=" + REQUIRED_FIELDS_IN_RESPONSE + "&appId=" + APP_ID + "&appKey=" + APP_KEY;

        //https://api.nutritionix.com/v1_1/search/che?fields=item_name%2Citem_id%2Cnf_calories%2Fnf_serving_size_qty%2Fnf_serving_size_unit&appId=08310541&appKey=b6d4734ba1b80bce25497956cfb60ca9
        Log.i("URL", URL + typedFoodName);
        client.get(request, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                prgDialog.hide();
                try {
                    if (null != response.getJSONArray("hits") && response.getJSONArray("hits").length() > 0) {
                        JSONArray hits = response.getJSONArray("hits");

                        autoSuggestedFoodAdapter.clear();

                        for (int i = 0; i < hits.length(); i++) {
                            String foodId = hits.getJSONObject(i).getJSONObject("fields").getString("item_id");
                            String foodName = hits.getJSONObject(i).getJSONObject("fields").getString("item_name");
                            Double dCalories = hits.getJSONObject(i).getJSONObject("fields").getDouble("nf_calories");
                            Double dQty = hits.getJSONObject(i).getJSONObject("fields").getDouble("nf_serving_size_qty");
                            String unit = hits.getJSONObject(i).getJSONObject("fields").getString("nf_serving_size_unit");

                            Long calories = Math.round(dCalories);
                            Long qty = Math.round(dQty);

                            Food food = new Food();
                            food.setFoodId(foodId);
                            food.setName(foodName);
                            food.setCalories(calories);
                            food.setAmount(qty);
                            food.setUnit(unit);

                            autoSuggestedFoodAdapter.add(food);
                            autoSuggestedFoods.add(food);
                        }
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                throwable.printStackTrace();
            }
        });
    }

    public void receiveBarcode(String detectedBarcode) {
        if (null != detectedBarcode) {
            Log.i("Barcode Value: ", detectedBarcode);
            getFoodByBarcode(detectedBarcode);
        }
    }

    private void getFoodByBarcode(String upcCode) {
        autoSuggestedFoods = new ArrayList<Food>();

        AsyncHttpClient client = new AsyncHttpClient();

        String request = UPC_URL + "?upc=" + upcCode + "&appId=" + APP_ID + "&appKey=" + APP_KEY;

        //https://api.nutritionix.com/v1_1/item?upc=49000036756&appId=08310541&appKey=b6d4734ba1b80bce25497956cfb60ca9
        client.get(request, null, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (null != response) {
                        String foodId = response.getString("item_id");
                        String foodName = response.getString("item_name");
                        Double dCalories = response.getDouble("nf_calories");
                        Double dQty = response.getDouble("nf_serving_size_qty");
                        String unit = response.getString("nf_serving_size_unit");

                        Long calories = Math.round(dCalories);
                        Long qty = Math.round(dQty);

                        Food food = new Food();
                        food.setFoodId(foodId);
                        food.setName(foodName);
                        food.setCalories(calories);
                        food.setAmount(qty);
                        food.setUnit(unit);

                        Log.i("Food Detected", foodName);
                        // Pass this food to Add Food Page
                    }

                } catch (JSONException e) {
                    Toast.makeText(getContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error Occured", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                throwable.printStackTrace();
            }
        });
    }

}

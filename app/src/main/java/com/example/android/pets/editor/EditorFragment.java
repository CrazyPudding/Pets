package com.example.android.pets.editor;

import android.app.Fragment;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.R;
import com.example.android.pets.data.PetContract;

/**
 * View 层
 */
public class EditorFragment extends Fragment implements EditorContract.View {

    private Uri mCurrentUri;
    private EditText mNameEditText;
    private EditText mBreedEditText;
    private EditText mWeightEditText;
    private Spinner mGenderSpinner;
    private EditorContract.Presenter mEditorPresenter;
    private boolean mPetHasChanged = false;
    private View.OnTouchListener mTouchListener= new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mPetHasChanged = true;
            return false;
        }
    };

    public boolean isPetChanged() {
        return mPetHasChanged;
    }

    public static EditorFragment newInstance() {
        Bundle args = new Bundle();

        EditorFragment fragment = new EditorFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        String uriString = getArguments().getString("currentUri");
        if (uriString != null) {
            mCurrentUri = Uri.parse(uriString);
            mEditorPresenter.start();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.editor_fragment, container, false);
        mNameEditText = (EditText) root.findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) root.findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) root.findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) root.findViewById(R.id.spinner_gender);

        mNameEditText.setOnTouchListener(mTouchListener);
        mBreedEditText.setOnTouchListener(mTouchListener);
        mWeightEditText.setOnTouchListener(mTouchListener);
        mGenderSpinner.setOnTouchListener(mTouchListener);

        chooseGender();

        setHasOptionsMenu(true);

        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_editor, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // 响应 Save 按钮
            case R.id.action_save:
                String nameString = mNameEditText.getText().toString().trim();
                String breedString = mBreedEditText.getText().toString().trim();
                String weightString = mWeightEditText.getText().toString().trim();
                mEditorPresenter.savePet(getActivity().getContentResolver(), mCurrentUri,
                        nameString, breedString, weightString);
                getActivity().finish();
                return true;
            // 响应 Delete 按钮
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            // 响应 app bar 中的 Up 按钮
            case android.R.id.home:
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(getActivity());
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(getActivity());
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null ) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
    }

    @Override
    public void chooseGender() {
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mEditorPresenter.setGender(PetContract.PetEntry.GENDER_MALE);
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mEditorPresenter.setGender(PetContract.PetEntry.GENDER_FEMALE);
                    } else {
                        mEditorPresenter.setGender(PetContract.PetEntry.GENDER_UNKNOWN);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mEditorPresenter.setGender(PetContract.PetEntry.GENDER_UNKNOWN); // Unknown
            }
        });
    }

    @Override
    public void showSavePetSuccess() {
        Toast.makeText(getActivity(), "Pet saved!" , Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showSavePetFailed() {
        Toast.makeText(getActivity(), "Pet saving failed!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUpdateSuccess() {
        Toast.makeText(getActivity(), "Pet updated", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUpdateFailed() {
        Toast.makeText(getActivity(), "Error with updating pet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDeleteSuccess() {
        Toast.makeText(getActivity(), R.string.editor_delete_pet_successful, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDeleteFailed() {
        Toast.makeText(getActivity(), R.string.editor_delete_pet_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mEditorPresenter.deletePet(getActivity().getContentResolver(), mCurrentUri);
                getActivity().finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public void showPetDetails(String nameString, String breedString, int weight, int gender) {
        mNameEditText.setText(nameString);
        mBreedEditText.setText(breedString);
        mWeightEditText.setText(String.valueOf(weight));

        switch (gender) {
            case PetContract.PetEntry.GENDER_MALE:
                mGenderSpinner.setSelection(1);
                break;
            case PetContract.PetEntry.GENDER_FEMALE:
                mGenderSpinner.setSelection(2);
                break;
            default:
                mGenderSpinner.setSelection(0);
                break;
        }
    }

    @Override
    public void setPresenter(EditorContract.Presenter presenter) {
        if (presenter != null) {
            mEditorPresenter = presenter;
        }
    }
}

package com.marionthefourth.augimas.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.util.ArrayMap;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.marionthefourth.augimas.R;
import com.marionthefourth.augimas.backend.Backend;
import com.marionthefourth.augimas.classes.objects.FirebaseEntity;
import com.marionthefourth.augimas.classes.objects.content.BrandingElement;
import com.marionthefourth.augimas.classes.objects.content.RecentActivity;
import com.marionthefourth.augimas.classes.objects.entities.Team;
import com.marionthefourth.augimas.classes.objects.entities.User;

import static com.marionthefourth.augimas.backend.Backend.getCurrentUser;
import static com.marionthefourth.augimas.classes.constants.Constants.Ints.SignificantNumbers.GENERAL_PADDING_AMOUNT;

/**
 * Created on 8/19/17.
 */

public final class ColorDialog extends AlertDialog.Builder {
    //    Dialog Constructor
    public ColorDialog(final int paletteIndex, final int colorIndex, final String hexCode, final BrandingElement brandingElement, final View containingView, final Activity activity) {
        super(containingView.getContext());
        setupDialog(paletteIndex,colorIndex, hexCode, brandingElement, containingView, activity);
    }
    //    Dialog Setup Methods
    private void setupDialog(final int paletteIndex, final int colorIndex, final String hexCode, final BrandingElement brandingElement, final View containingView, final Activity activity) {
        final TextInputEditText editText = new TextInputEditText(containingView.getContext());
        final View view = new View(getContext());
        view.setBackgroundColor(Color.BLACK);

        if (hexCode != null && !hexCode.equals("")) {
            editText.setText(hexCode.substring(1));
            view.setBackgroundColor(Color.parseColor(hexCode));
            setTitle(containingView.getContext().getString(R.string.title_update_color));
            setupPositiveButton(paletteIndex,colorIndex,hexCode,brandingElement,editText,R.string.button_text_update,activity);
            setNegativeButton(R.string.delete_text, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElement.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                final BrandingElement brandingElementItem = new BrandingElement(dataSnapshot);
                                String[] items = brandingElementItem.getData().get(paletteIndex).split(",");
                                for (int i = 0; i < items.length; i++) {
                                    if (items[i].equals(hexCode)) {
                                        if (items.length > 1) {
                                            if (i == items.length-1) {
                                                brandingElementItem.getData().set(paletteIndex,brandingElementItem.getData().get(paletteIndex).replace(","+hexCode,""));
                                                break;
                                            } else {
                                                brandingElementItem.getData().set(paletteIndex,brandingElementItem.getData().get(paletteIndex).replace(hexCode+",",""));
                                                break;
                                            }
                                        } else {
                                            brandingElementItem.getData().set(paletteIndex,brandingElementItem.getData().get(paletteIndex).replace(hexCode,""));
                                            break;
                                        }
                                    }
                                }
                                sendBrandingElementNotification(brandingElementItem, RecentActivity.ActivityVerbType.REMOVE,activity);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            });
        } else {
            setTitle(containingView.getContext().getString(R.string.title_create_color));
            setupPositiveButton(paletteIndex,colorIndex,hexCode,brandingElement,editText,R.string.button_text_create,activity);
        }

        setupDialogLayout(new TextInputLayout(containingView.getContext()),editText,view);

        show();
    }
    private void setupPositiveButton(final int paletteIndex, final int colorIndex, final String hexCode, final BrandingElement brandingElement, final TextInputEditText editText, final int buttonText, final Activity activity) {
        setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if (!editText.getText().toString().equals("") && editText.getText().length() == 6 && containsOnlyProperInput(editText)) {
                    Backend.getReference(R.string.firebase_branding_elements_directory, activity).child(brandingElement.getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChildren()) {
                                final BrandingElement brandingElementItem = new BrandingElement(dataSnapshot);
                                modifyItem(brandingElementItem,paletteIndex,colorIndex,hexCode,editText.getText().toString(),activity);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
            }
        });
    }
    private boolean containsOnlyProperInput(TextInputEditText editText) {
        String[] bannedInput = {"q","w","r","t","y","u","i","o","p","s","g","h","j","k","l","z","x","v","n","m"};

        for (String bannedChar:bannedInput) {
            if (editText.getText().toString().contains(bannedChar)) {
                return false;
            }
        }

        return true;
    }
    private void modifyItem(final BrandingElement element, final int paletteIndex, final int colorIndex, final String previousText, final String newText, final Activity activity) {
        String paletteData = "";

        if (element.getData().size() > paletteIndex) {
           paletteData = element.getData().get(paletteIndex);
            if (previousText != null && !previousText.equals("")) {
                paletteData = paletteData.replace(previousText,"#"+newText);
                element.getData().set(paletteIndex,paletteData);
                sendBrandingElementNotification(element, RecentActivity.ActivityVerbType.UPDATE,activity);
            } else {
                paletteData += ",#"+newText;
                element.getData().set(paletteIndex,paletteData);
                sendBrandingElementNotification(element, RecentActivity.ActivityVerbType.ADD,activity);
            }
        } else {
            paletteData += "#"+newText;
            element.getData().add(paletteData);
            sendBrandingElementNotification(element, RecentActivity.ActivityVerbType.ADD,activity);

        }

        // Find position of data to replace


    }
    private void setupDialogLayout(final TextInputLayout layout, final TextInputEditText editText, View view) {
        final LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT,GENERAL_PADDING_AMOUNT);

        final LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        final LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                150,150
        );

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            view.setForegroundGravity(Gravity.CENTER);
        }
        view.setLayoutParams(layoutParams1);

        linearLayout.addView(view);

        editText.setLayoutParams(layoutParams);
        editText.setEnabled(true);
        editText.setFilters(new InputFilter[] { new InputFilter.LengthFilter(6) } );
        editText.setHint(getContext().getString(R.string.hex_text));
        layout.addView(editText,0,layoutParams);
        linearLayout.addView(layout);

        setView(linearLayout);

    }
    //    Functional Methods
    private void sendBrandingElementNotification(final BrandingElement brandingName, final RecentActivity.ActivityVerbType verbType,final Activity activity) {
        if ((getCurrentUser() != null ? getCurrentUser().getUID():null) != null) {
            Backend.getReference(R.string.firebase_teams_directory,activity).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final ArrayMap<FirebaseEntity.EntityType,Team> teamMap = Team.toClientAndHostTeamMap(dataSnapshot,brandingName.getTeamUID());
                    Backend.getReference(R.string.firebase_users_directory,activity).child(getCurrentUser().getUID()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final User currentUser = new User(dataSnapshot);

                            if (currentUser.hasInclusiveAccess(FirebaseEntity.EntityRole.EDITOR)) {
                                final RecentActivity hostRecentActivity;
                                final RecentActivity clientRecentActivity;

                                switch (verbType) {
                                    case UPDATE:
                                        if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName());
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType);
                                        }
                                        break;
                                    default:
                                        if (currentUser.getType() == FirebaseEntity.EntityType.HOST) {
                                            hostRecentActivity = new RecentActivity(currentUser,brandingName, verbType, teamMap.get(FirebaseEntity.EntityType.CLIENT).getName());
                                            clientRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.HOST),brandingName, verbType);
                                        } else {
                                            hostRecentActivity = new RecentActivity(teamMap.get(FirebaseEntity.EntityType.CLIENT),brandingName, verbType);
                                            clientRecentActivity = new RecentActivity(currentUser,brandingName, verbType);
                                        }
                                        break;
                                }

                                Backend.sendUpstreamNotification(hostRecentActivity,teamMap.get(FirebaseEntity.EntityType.HOST).getUID(),currentUser.getUID(),brandingName.getType().toString(),activity, true);
                                Backend.sendUpstreamNotification(clientRecentActivity,teamMap.get(FirebaseEntity.EntityType.CLIENT).getUID(),currentUser.getUID(),brandingName.getType().toString(),activity, true);

                                Backend.update(brandingName, activity);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {}
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
        }
    }

}
package com.wzq.jz_app.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wzq.jz_app.R;
import com.wzq.jz_app.base.BaseFragment;
import com.wzq.jz_app.common.Constants;
import com.wzq.jz_app.model.bean.local.BBill;
import com.wzq.jz_app.model.bean.local.BSort;
import com.wzq.jz_app.model.bean.local.NoteBean;
import com.wzq.jz_app.model.bean.remote.MyUser;
import com.wzq.jz_app.model.repository.BmobRepository;
import com.wzq.jz_app.model.repository.LocalRepository;
import com.wzq.jz_app.ui.activity.AboutActivity;
import com.wzq.jz_app.ui.activity.LoginActivity;
import com.wzq.jz_app.ui.activity.SettingActivity;
import com.wzq.jz_app.ui.activity.SortActivity;
import com.wzq.jz_app.ui.activity.UserInfoActivity;
import com.wzq.jz_app.ui.adapter.MainFragmentPagerAdapter;
import com.wzq.jz_app.utils.Base64BitmapUtils;
import com.wzq.jz_app.utils.ExcelUtil;
import com.wzq.jz_app.utils.FileProvider7;
import com.wzq.jz_app.utils.FileUtil;
import com.wzq.jz_app.utils.ImageUtils;
import com.wzq.jz_app.utils.OSUtil;
import com.wzq.jz_app.utils.SelectphotoUtils;
import com.wzq.jz_app.utils.SharedPUtils;
import com.wzq.jz_app.utils.ThemeManager;
import com.wzq.jz_app.widget.ButtomDialogView1;
import com.wzq.jz_app.widget.CircleImageView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import static android.app.Activity.RESULT_OK;
import static cn.bmob.v3.Bmob.getApplicationContext;
import static com.wzq.jz_app.utils.SelectphotoUtils.getRealFilePathFromUri;


/**
 * ?????????wzq on 2019/4/2.
 * ?????????wang_love152@163.com
 */

public class MineFragment extends BaseFragment implements View.OnClickListener {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private TextView tOutcome;
    private TextView tIncome;
    private TextView tTotal;

    private View drawerHeader;
    private CircleImageView drawerIv;
    private TextView drawerTvAccount, drawerTvMail;

    protected static final int USERINFOACTIVITY_CODE = 0;
    protected static final int LOGINACTIVITY_CODE = 1;

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static final int CROP_SMALL_PICTURE = 2;
    // ????????????????????????
    private int type = 1;//"1"?????? "2"??????
    //????????????????????????
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    //????????????????????????
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    //????????????
    protected static Uri tempUri = null;
    public final int SIZE = 2 * 1024;


    // Tab
    private FragmentManager mFragmentManager;
    private MainFragmentPagerAdapter mFragmentPagerAdapter;
    private HomeFragment homeFragment;
    private ChartFragment chartFragment;

    private File selectFile;
    private ButtomDialogView1 dialogView1;
    private MyUser currentUser;
    private RelativeLayout editor;
    private RelativeLayout snyc;
    private RelativeLayout setting;
    private RelativeLayout theme;
    private RelativeLayout about;
    private RelativeLayout countClass;
    private RelativeLayout nav_outexcle;


    /***************************************************************************/
    @Override
    protected int getLayoutId() {
        return R.layout.main_fragment_mine;
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        super.initData(savedInstanceState);
        //?????? EventBus
        EventBus.getDefault().register(this);

        //??????????????????????????????????????????????????????
        if (SharedPUtils.isFirstStart(mContext)) {
            Log.i(TAG, "??????????????????????????????????????????????????????");
            NoteBean note = new Gson().fromJson(Constants.BILL_NOTE, NoteBean.class);
            List<BSort> sorts = note.getOutSortlis();
            sorts.addAll(note.getInSortlis());
            LocalRepository.getInstance().saveBsorts(sorts);
            LocalRepository.getInstance().saveBPays(note.getPayinfo());
        }

        homeFragment = new HomeFragment();
    }

    @Override
    protected void initWidget(Bundle savedInstanceState) {
        super.initWidget(savedInstanceState);


        drawerHeader = getViewById(R.id.drawer_header);
        drawerIv = getViewById(R.id.drawer_iv);
        drawerTvAccount = getViewById(R.id.drawer_tv_name);
        drawerTvMail = getViewById(R.id.drawer_tv_email);


        editor = getViewById(R.id.nav_edit);//????????????
        snyc = getViewById(R.id.nav_sync);//????????????
        setting = getViewById(R.id.nav_setting);//??????
        theme = getViewById(R.id.nav_theme);//??????
        countClass = getViewById(R.id.nav_class);//????????????
        nav_outexcle = getViewById(R.id.nav_outexcle);//????????????
        about = getViewById(R.id.nav_about);//??????

        //??????????????????
        setDrawerHeaderAccount();
        if (currentUser != null) {//????????????
            getHeadimg();
            Log.e("123", "??????: " + currentUser.getImage());
        }
    }

    @Override
    protected void setListener() {

    }

    @Override
    protected void initClick() {
        super.initClick();

        //??????????????????????????????
        drawerHeader.setOnClickListener(v -> {
            if (currentUser == null) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("exit", "1");
                startActivity(intent);
                EventBus.getDefault().post("finish");
            } else {
                toChooseImg();
            }
        });

        //?????????????????????
        editor.setOnClickListener(this);
        snyc.setOnClickListener(this);
        setting.setOnClickListener(this);
        theme.setOnClickListener(this);
        about.setOnClickListener(this);
        countClass.setOnClickListener(this);
        nav_outexcle.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.nav_edit://????????????
                currentUser = BmobUser.getCurrentUser(MyUser.class);
                //??????????????????
                if (currentUser == null)
//                    SnackbarUtils.show(mContext, "????????????");
                    Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                else
                    startActivityForResult(new Intent(mContext, UserInfoActivity.class), USERINFOACTIVITY_CODE);
                break;

            case R.id.nav_class://????????????
                startActivity(new Intent(mContext, SortActivity.class));
                break;
            case R.id.nav_outexcle://????????????
                ActivityCompat.requestPermissions(getActivity(), new String[]{android
                        .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                //???????????????
                if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && getAvailableStorage(getContext()) > 1000000) {
                    Toast.makeText(getContext(), "SD????????????", Toast.LENGTH_LONG).show();
                    return;
                }
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/AndroidwzqExcelDemo/";
                File file = new File(filePath);
                if (!file.exists()) {
                    file.mkdirs();
                    if (file.mkdirs()) {
                        Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "??????????????????", Toast.LENGTH_SHORT).show();
                    }
                }
                String excleCountName = "/wzq.xls";
                String[] title = {"??????id", "????????????id", "??????", "??????", "??????id", "????????????", "??????", "????????????", "????????????", "????????????", "????????????", "??????"};
                String sheetName = "demoSheetName";
                List<BBill> bBills = new ArrayList<>();
                bBills = LocalRepository.getInstance().getBBills();
                filePath = file.getAbsolutePath() + "/" + excleCountName;
                ExcelUtil.initExcel(filePath, title);
                ExcelUtil.writeObjListToExcel(bBills, filePath, getApplicationContext());

                break;

            case R.id.nav_sync://????????????
                if (currentUser == null)
                    Toast.makeText(getApplicationContext(), "????????????", Toast.LENGTH_SHORT).show();
                else
                    BmobRepository.getInstance().syncBill(currentUser.getObjectId());
                break;
            case R.id.nav_setting://??????

                startActivity(new Intent(mContext, SettingActivity.class));
                break;
            case R.id.nav_theme://??????
                showUpdateThemeDialog();
                break;
            case R.id.nav_about://??????
                startActivity(new Intent(mContext, AboutActivity.class));
                break;
        }
    }

    /**
     * ????????????????????? Dialog
     */
    private void showUpdateThemeDialog() {
        String[] themes = ThemeManager.getInstance().getThemes();
        new MaterialDialog.Builder(mContext)
                .title("????????????")
                .titleGravity(GravityEnum.CENTER)
                .items(themes)
//                .titleColorRes(R.color.material_red_500)
                .contentColor(Color.BLACK) // notice no 'res' postfix for literal color
                .linkColorAttr(R.attr.aboutPageHeaderTextColor)  // notice attr is used instead of none or res for attribute resolving
                .dividerColorRes(R.color.colorMainDateBg)
//                .backgroundColorRes(R.drawable.dialog_backgroud)//?????????
//                .positiveColorRes(R.color.material_red_500)
                .neutralColorRes(R.color.colorControlNormal)
//                .negativeColorRes(R.color.material_red_500)
//                .widgetColorRes(R.color.colorControlNormal)//????????????
//                .buttonRippleColorRes(R.color.colorControlNormal)
                .negativeText("??????")
//                .customView(R.layout.activity_dialog,true)
                .itemsCallbackSingleChoice(0, (dialog, itemView, position, text) -> {
                    ThemeManager.getInstance().setTheme(mActivity, themes[position]);
                    dialog.dismiss();
                    return false;
                }).show();
    }


    /**
     * ?????????????????????????????????
     */
    private void toChooseImg() {
        if (dialogView1 == null) {
            dialogView1 = new ButtomDialogView1(getActivity());
            dialogView1.setOnDialogClickListener(new ButtomDialogView1.OnDialogClickListener() {
                @Override
                public void onclick1() {//??????
                    selectFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis()+"jz_app.jpg");
                    SelectphotoUtils.takePicture(getActivity(), MineFragment.this, selectFile);
                    dialogView1.dismiss();
                }

                @Override
                public void onclick2() {//????????????
                    Intent openAlbumIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    openAlbumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
                    dialogView1.dismiss();
                }

                @Override
                public void onclick3() {
                    dialogView1.dismiss();
                }
            });
        }
        dialogView1.show();
    }


    /**
     * ??????SD????????????
     */
    private static long getAvailableStorage(Context context) {
        String root = context.getExternalFilesDir(null).getPath();
        StatFs statFs = new StatFs(root);
        long blockSize = statFs.getBlockSize();
        long availableBlocks = statFs.getAvailableBlocks();
        long availableSize = blockSize * availableBlocks;
        // Formatter.formatFileSize(context, availableSize);
        return availableSize;
    }

    /**
     * ??????Activity?????????
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case CHOOSE_PICTURE:// ?????????????????????
                try {
                    selectFile  = new File(Environment.getExternalStorageDirectory(), "jz_app.jpg");
                    SelectphotoUtils.startPhotoZoom(data.getData(), MineFragment.this, selectFile);
                } catch (NullPointerException e) {
                    e.printStackTrace();// ????????????????????????
                }
                break;
            case TAKE_PICTURE:// ??????????????????
                if (isExistSd()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //?????????7.0???????????? ?????? ?????????uri??????
                        Uri fileUri = FileProvider7.getUriForFile(getActivity(), selectFile);
                        if (selectFile.exists()) {
                            //?????????????????????????????????
                            SelectphotoUtils.startPhotoZoom(fileUri, MineFragment.this, selectFile);
                        }
                    } else {
                        if (selectFile.exists()) {
                            //?????????????????????????????????
                            SelectphotoUtils.startPhotoZoom(Uri.fromFile(selectFile), MineFragment.this, selectFile);
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "??????????????????", Toast.LENGTH_SHORT).show();
                }
                break;
            case CROP_SMALL_PICTURE://????????????????????????
                if (data != null) {
                    setPicToView(data);
                }
                ;
                break;
        }
    }

    /**
     * ??????????????????SD???????????????
     */
    private boolean isExistSd() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return true;
        } else
            return false;
    }

    /**
     * ?????????????????????????????????
     *
     * @param data
     */
    protected void setPicToView(Intent data) {
        Bundle extras = data.getExtras();
        Bitmap bitmap = null;
        try {
            if (OSUtil.isMIUI()) {
                Uri uritempFile = Uri.parse("file://" + "/" + selectFile);
                try {
                    bitmap = BitmapFactory.decodeStream(getActivity().getContentResolver().openInputStream(uritempFile));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            } else {
                if (extras != null) {
                    bitmap = extras.getParcelable("data");

                }
            }
            //??????????????????
            File filepath = SelectphotoUtils.saveFile(bitmap, Environment.getExternalStorageDirectory().toString(), "jz_app.jpg");
            //?????????????????????bitMap?????????????????????????????????
            Toast.makeText(getActivity(), "?????????", Toast.LENGTH_SHORT).show();
            uploadPic(bitmap);
        } catch (Exception e) {

        }
    }


    /**
     * ??????DrawerHeader???????????????
     */
    public void setDrawerHeaderAccount() {
        currentUser = BmobUser.getCurrentUser(MyUser.class);
        //??????????????????
        if (currentUser != null) {
            drawerTvAccount.setText(currentUser.getUsername());
            drawerTvMail.setText(currentUser.getEmail());

            RequestOptions requestOptions = new RequestOptions();
            requestOptions.placeholder(R.drawable.icon_head_error);
//            Glide.with(this).load(currentUser.getImage()).apply(requestOptions).into(drawerIv);

        } else {
            drawerTvAccount.setText("??????");
            drawerTvMail.setText("????????????");
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100:
                selectFile = new File(Environment.getExternalStorageDirectory(), "jz_app.jpg");
                SelectphotoUtils.takePicture(getActivity(), MineFragment.this, selectFile);
                break;

            case 101:
                selectFile = new File(Environment.getExternalStorageDirectory(), "jz_app.jpg");
                SelectphotoUtils.takePicture(getActivity(), MineFragment.this, selectFile);
                break;
            default:
        }
    }

    /**
     * ??????????????????????????????
     *
     * @param bitmap
     */
    private void uploadPic(Bitmap bitmap) {
        // ??????????????????
        // ??????????????????Bitmap?????????file???????????????file???url????????????????????????
        // ???????????????????????????????????????????????????
        // bitmap??????????????????????????????????????????????????????
        String imagename = currentUser.getObjectId() + "_" + String.valueOf(System.currentTimeMillis());
        String imagePath = ImageUtils.savePhoto(bitmap, Environment
                .getExternalStorageDirectory().getAbsolutePath(), imagename + ".png");
        if (imagePath != null) {
            final BmobFile bmobFile = new BmobFile(new File(imagePath));
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        MyUser newUser = new MyUser();
                        newUser.setImage(bmobFile.getFileUrl());
                        newUser.update(currentUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(getActivity(), "????????????", Toast.LENGTH_SHORT).show();
                                    drawerIv.setImageBitmap(bitmap);
                                    final String img = Base64BitmapUtils.bitmapToBase64(bitmap);
                                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bill/head/img/";
                                    FileUtil.writeToFile(filePath, "data:image/png;base64," + img);
                                } else {
                                    Toast.makeText(getActivity(), "????????????," + e.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "????????????," + e.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    private void getHeadimg() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Bill/head/img/";
        String img = FileUtil.readFile(filePath);
        if (!TextUtils.isEmpty(img)) {
            Bitmap bitmap = Base64BitmapUtils.stringToBitmap(img);
            drawerIv.setImageBitmap(bitmap);
        }
    }


    //EvenBus
    @Subscribe
    public void eventBusListener(String tag) {
        if (TextUtils.equals("1", tag)) {
            processLogic();
        }
    }
}

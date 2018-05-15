package me.bakumon.moneykeeper.binding;

import android.content.Context;
import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * binding 属性适配器（自动被 DataBinding 引用）
 *
 * @author Bakumon https://bakumon.me
 */
public class BindAdapter {

    @BindingAdapter("android:visibility")
    public static void showHide(View view, boolean show) {
        view.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @BindingAdapter("text_check_null")
    public static void setText(TextView textView, String text) {
        boolean isEmpty = TextUtils.isEmpty(text);
        if (!isEmpty) {
            textView.setText(text);
        }
        textView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }

    @BindingAdapter("src_img_name")
    public static void setImg(ImageView imageView, String imgName) {
        Context context = imageView.getContext();
        if (!TextUtils.isEmpty(imgName)) {
            int resId = context.getResources().getIdentifier(imgName, "mipmap", context.getPackageName());
            imageView.setImageResource(resId);
        }
    }
}
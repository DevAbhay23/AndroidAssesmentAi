package com.assignment.assesments.views

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.ImageView
import com.assignment.assesments.R
import com.squareup.picasso.Picasso


class ProgressDialog(mActivity: Activity) {
    private var activity = mActivity
    private var dialog: Dialog? = null

    fun showDialog() {
        dialog = Dialog(activity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.custom_loader)

        val gifImageView: ImageView = dialog!!.findViewById(R.id.custom_loading_imageView)
        Picasso.get()
            .load(R.drawable.progress_animation)
            .error(R.drawable.progress_animation)
            .placeholder(R.drawable.progress_animation).into(gifImageView)

        dialog!!.show()
    }

    fun hideDialog() {
        dialog!!.dismiss()
    }
}

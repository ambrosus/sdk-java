/*
 * Copyright: Ambrosus Technologies GmbH
 * Email: tech@ambrosus.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files
 * (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.ambrosus.ambviewer

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import com.ambrosus.ambviewer.utils.FragmentSwitchHelper
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(),
        PermissionRationaleFragment.PermissionDialogListener {

    companion object {
        val TAG = "MainActivity"
        val PERMISSION_REQ = 25

    }

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        supportFragmentManager.addOnBackStackChangedListener {
            updateBackButtonState()
        }

        FragmentSwitchHelper.addChild(this, MainScannerFragment(), R.id.contentContainer)
    }

    override fun onBackPressed() {
        if(!FragmentSwitchHelper.goBack(supportFragmentManager, R.id.contentContainer))
            confirmQuit()
    }

    private fun canGoBack() = supportFragmentManager.backStackEntryCount > 0

    private fun confirmQuit() {
        AlertDialog.Builder(this, R.style.AlertDialogCustom).setTitle(R.string.dialog_title_quit_app)
                .setNegativeButton(getString(R.string.dialog_cancel), { dialog, which -> dialog.dismiss() })
                .setPositiveButton(getString(R.string.dialog_quit), { dialog, which -> finish() }).show()

    }

    fun updateBackButtonState(){
        supportActionBar!!.setDisplayHomeAsUpEnabled(canGoBack())
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item!!.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item);
    }

    override fun onRequestPermission(permission: String) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQ)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQ -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // We have been granted the Manifest.permission.WRITE_EXTERNAL_STORAGE permission. Now we may proceed with exporting.
                } else {
                    Toast.makeText(this, R.string.no_required_permission, Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

}

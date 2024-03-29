package com.contentstack.contentstack_android_ecommerce_app

import android.app.ProgressDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.contentstack.contentstack_android_ecommerce_app.Constants.CURRENCY
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_ADDRESS_LINE1
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMERCITY
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_COUNTRY
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_MOBILE
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_POSTAL_CODE
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_REGION
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_EMAIL
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_GIVEN_NAME
import com.contentstack.contentstack_android_ecommerce_app.Constants.CUSTOMER_REF_ID
import com.contentstack.contentstack_android_ecommerce_app.Constants.DARK_MODE
import com.contentstack.contentstack_android_ecommerce_app.Constants.ENVIRONMENT
import com.contentstack.contentstack_android_ecommerce_app.Constants.LABEL
import com.contentstack.contentstack_android_ecommerce_app.Constants.LANDMARK
import com.contentstack.contentstack_android_ecommerce_app.Constants.LOCALE
import com.contentstack.contentstack_android_ecommerce_app.Constants.ORDER_REF_ID
import com.contentstack.contentstack_android_ecommerce_app.Constants.RECEIVER_NAME
import com.contentstack.contentstack_android_ecommerce_app.Constants.RECEIVER_PHONE
import com.contentstack.contentstack_android_ecommerce_app.Constants.SITE_NAME
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.Customer
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.Item
import com.contentstack.contentstack_android_ecommerce_app.data.dataclasses.OrdersRequest
import com.contentstack.contentstack_android_ecommerce_app.utils.ResourceStatus
import com.contentstack.contentstack_android_ecommerce_app.utils.StatusType
import com.contentstack.contentstack_android_ecommerce_app.viewmodels.OrdersViewModel
import com.squareup.picasso.Picasso
import id.durianpay.android.Durianpay
import id.durianpay.android.Interfaces.CheckoutResultListener
import id.durianpay.android.model.DCheckoutOptions
import id.durianpay.android.model.DPaymentFailed
import id.durianpay.android.model.DPaymentSuccess
import kotlinx.android.synthetic.main.activity_product.*
import kotlinx.android.synthetic.main.lamp_recycler_view_item.previewIcon
import kotlinx.android.synthetic.main.price_review_title_section.*
import kotlinx.android.synthetic.main.suggested_for_you_section.*


@RequiresApi(Build.VERSION_CODES.LOLLIPOP)
class ProductActivity : AppCompatActivity(), CheckoutResultListener{

    val TAG = ProductActivity::class.java.simpleName
    private lateinit var viewModel: OrdersViewModel
    var progressBar: ProgressDialog? = null
    var amount = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)

        progressBar = ProgressDialog(this)
        progressBar!!.setTitle("Wait!")
        progressBar!!.setMessage("loading ...")

        val lamp = intent.getSerializableExtra("lamp") as? Lamp
        viewModel = ViewModelProviders.of(this)[OrdersViewModel::class.java]

        if (lamp!=null){
            Log.e("lamp", lamp.toString())
            loadViews(lamp)
        }

        setupObservers();
        btnPurchase.setOnClickListener {
            var item: List<Item>? = null
            if (lamp!=null) {
                amount = lamp.price;
                 item = listOf(
                     Item(
                         name = lamp.title,
                         qty = 1,
                         price = lamp.price
                     )
                 )

                val customer = Customer(
                    null,
                    CUSTOMER_REF_ID,
                    CUSTOMER_EMAIL,
                    CUSTOMER_GIVEN_NAME
                )
                val order = OrdersRequest(null,
                    lamp.price,
                    CURRENCY,
                    customer,
                    null,
                    items = item,
                    order_ref_id = ORDER_REF_ID
                )

                setupListeners(order);
            }
        }
    }

    private fun setupListeners(order: OrdersRequest) {
        val mainIntent = Intent(Intent.ACTION_MAIN, null)
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER)
        val installedList = this.getPackageManager().queryIntentActivities(mainIntent, 0)

        viewModel.callOrdersData(this, order)
    }

    private fun setupObservers() {
        viewModel.apiResponseData.observe(this, Observer {
            Log.d(TAG, viewModel.apiResponseData.toString())
            val checkoutOptions = DCheckoutOptions()
            addCheckoutOptions(checkoutOptions, it.data.access_token, it.data.id)
            Durianpay.getInstance(this)
                .checkout(checkoutOptions, this)
        })
        viewModel.apiCallStatus.observe(this, Observer {
            processStatus(it)
        })
    }

    private fun loadViews(lamp: Lamp) {
        Picasso.get().load(lamp.image).into(previewIcon)
        titleLamp.text = lamp.title
        reviewLamp.text =   "${lamp.price} Review"
        productPrice.text = "Rp ${lamp.price}"

        loadSuggested4You()
    }


    private fun loadSuggested4You(){
        val allLampList: ArrayList<Lamp> = getAllLamps()
        recyclerView.layoutManager = LinearLayoutManager(this@ProductActivity, LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = SuggestedItemAdapter(lamps = allLampList)
    }

    private fun processStatus(resource: ResourceStatus) {
        //check the returned status code of api hit
        when (resource.status) {
            StatusType.SUCCESS -> {
                Toast.makeText(this, "Api hit Successful", Toast.LENGTH_SHORT).show()
                progressBar?.dismiss()
            }
            StatusType.EMPTY_RESPONSE -> {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
                progressBar?.dismiss()
            }
            StatusType.PROGRESSING -> {
                progressBar?.show()
            }
            StatusType.ERROR -> {
                Toast.makeText(this, "Please try again", Toast.LENGTH_SHORT).show()
            }
            StatusType.LOADING_MORE -> {
                Toast.makeText(this, "Loading......", Toast.LENGTH_SHORT).show()
                progressBar?.dismiss()
            }
            StatusType.NO_NETWORK -> {
                Toast.makeText(this, "Please check internet connection", Toast.LENGTH_SHORT).show()
            }
            StatusType.SESSION_EXPIRED -> {
                Toast.makeText(this, "Session Expired", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onSuccess(p0: DPaymentSuccess?) {
        Log.d("callbackSuccess", p0.toString())
        Toast.makeText(this, "Payment Success" + "\n" + p0, Toast.LENGTH_LONG).show()
    }

    override fun onFailure(p0: DPaymentFailed?) {
        Log.d("callbackFailure", p0.toString())
        Toast.makeText(this, "Payment Failed" + "\n" + p0, Toast.LENGTH_SHORT).show()
    }

    override fun onClose(s: String?) {
        Toast.makeText(this, "Payment closed before completion" + "\n" + s, Toast.LENGTH_SHORT)
            .show()
        Log.d("payment closed", s.toString());
    }


    fun addCheckoutOptions(checkoutOptions: DCheckoutOptions, accessToken: String, orderId: String) {
        checkoutOptions.environment = ENVIRONMENT
        checkoutOptions.locale = LOCALE
        checkoutOptions.siteName = SITE_NAME
        checkoutOptions.customerId = CUSTOMER_REF_ID
        checkoutOptions.orderId = orderId
        checkoutOptions.accessToken = accessToken
        checkoutOptions.customerEmail = CUSTOMER_EMAIL
        checkoutOptions.customerGivenName = CUSTOMER_GIVEN_NAME
        checkoutOptions.amount = amount
        checkoutOptions.currency = CURRENCY
        checkoutOptions.label = LABEL
        checkoutOptions.customerAddressLine1 = CUSTOMER_ADDRESS_LINE1
        checkoutOptions.customerCity = CUSTOMERCITY
        checkoutOptions.customerCountry = CUSTOMER_COUNTRY
        checkoutOptions.customerRegion = CUSTOMER_REGION
        checkoutOptions.customerPostalCode = CUSTOMER_POSTAL_CODE
        checkoutOptions.receiverName = RECEIVER_NAME
        checkoutOptions.receiverPhone = RECEIVER_PHONE
        checkoutOptions.customerMobile = CUSTOMER_MOBILE
        checkoutOptions.landmark = LANDMARK
        checkoutOptions.isDarkMode = DARK_MODE
        checkoutOptions.isForceFail = true
        checkoutOptions.delayMs = 3000
    }
}

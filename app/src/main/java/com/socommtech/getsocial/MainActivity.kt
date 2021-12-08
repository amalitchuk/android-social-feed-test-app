package com.socommtech.getsocial

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.socommtech.getsocial.databinding.ActivityMainBinding
import im.getsocial.sdk.GetSocial
import im.getsocial.sdk.notifications.NotificationsQuery
import im.getsocial.sdk.ui.GetSocialUi
import im.getsocial.sdk.ui.pushnotifications.NotificationCenterViewBuilder

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    var features = Features(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initialize()
        with(binding) {
            btnSocialUi.setOnClickListener(features.smartInvites)
            btnFeed.setOnClickListener(features.activityFeed)
            btnSocial.setOnClickListener(features.social)
            btnPost.setOnClickListener(features.post)
            btnGroup.setOnClickListener(features.group)
            btnGroupMembers.setOnClickListener(features.groupMember)
            btnLink.setOnClickListener(features.triggerLink)
            btnCustomEvent.setOnClickListener(features.customEvent)
            btnCoupons.setOnClickListener(features.coupons)
            btnCouponsCreate.setOnClickListener(features.couponsCreate)
            btnFacebook.setOnClickListener(features.facebookIdentifier)
        }
    }

    private fun initialize() {
        GetSocial.addOnInitializeListener {
            "Console is initialized".consolePrint()
            "GetSocial initialized".consolePrint()
        }
        GetSocial.init()
        val query = NotificationsQuery.withAllStatuses()
        NotificationCenterViewBuilder.create(query).show()
        GetSocialUi.loadConfiguration("getsocial-dark/ui-config.json")
    }
}
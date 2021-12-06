package com.socommtech.getsocial

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jraska.console.Console
import com.jraska.console.Console.Companion.writeLine
import com.socommtech.getsocial.databinding.ActivityMainBinding
import im.getsocial.sdk.*
import im.getsocial.sdk.ui.invites.InvitesViewBuilder
import im.getsocial.sdk.common.PagingQuery
import im.getsocial.sdk.common.PagingResult
import im.getsocial.sdk.communities.*
import im.getsocial.sdk.invites.InviteChannelIds
import im.getsocial.sdk.invites.InviteContent
import im.getsocial.sdk.invites.LinkParams
import im.getsocial.sdk.media.MediaAttachment
import im.getsocial.sdk.promocodes.PromoCode
import im.getsocial.sdk.promocodes.PromoCodeContent


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialize()

        with(binding) {
            btnSocialUi.setOnClickListener(smartInvites)
            btnSocial.setOnClickListener(social)
            btnPost.setOnClickListener(post)
            btnGroup.setOnClickListener(group)
            btnGroupMembers.setOnClickListener(groupMember)
            btnLink.setOnClickListener(triggerLink)
            btnCustomEvent.setOnClickListener(customEvent)
            btnCoupons.setOnClickListener(coupons)
            btnCouponsCreate.setOnClickListener(couponsCreate)
        }


    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
    }


    private fun initialize() {
        GetSocial.addOnInitializeListener {
            "Console is initialized".consolePrint()
            "GetSocial initialized".consolePrint()
        }
    }

    private val social: View.OnClickListener = View.OnClickListener {
        var currentUser = GetSocial.getCurrentUser()

        val query = UsersQuery.find("John")
        val pagingQuery = PagingQuery(query)
        Communities.getUsers(pagingQuery, { result: PagingResult<User> ->
            val users = result.entries
            "Users with name John: $users".consolePrint()
        }, { error: GetSocialError ->
            Log.d("Communities", "Failed to get users: $error")
        })
    }

    private val post = View.OnClickListener {
        val query = TopicsQuery.all()
        val pagingQuery = PagingQuery(query)
        Communities.getTopics(pagingQuery, { result: PagingResult<Topic> ->
            val topics = result.entries
            Log.d("Communities", "Topics: $topics")
            "Topics: $topics".consolePrint()
        }, { error: GetSocialError ->
            Log.d("Communities", "Failed to get list of topics: $error")
        })
    }

    private val group = View.OnClickListener {
        val query = GroupsQuery.all()
        val pagingQuery = PagingQuery(query)
        Communities.getGroups(pagingQuery, { result: PagingResult<Group> ->
            val group = result.entries
            "Groups: $group".consolePrint()
        }, { error: GetSocialError ->
            print("Failed to get list of groups: $error")
        })
    }

    private val groupMember = View.OnClickListener {
        val groupId = "1"
        val query = MembersQuery.ofGroup(groupId)
        val pagingQuery = PagingQuery(query)
        Communities.getGroupMembers(pagingQuery, { result ->
            print("Members of group: $result.entries")
            "Members of group: ${result.entries}".consolePrint()
        }, { error ->
            print("Failed to get group members, error: $error")
        })
    }


    private val smartInvites: View.OnClickListener = View.OnClickListener {
        val wasShown = InvitesViewBuilder.create().show()
        println("GetSocial Smart Invites UI was shown: ${wasShown}")
    }

    private val triggerLink = View.OnClickListener {

        val linkParams = mapOf(
            "custom_key" to "custom_value", // custom key
            LinkParams.KEY_CUSTOM_TITLE to "Lorem ipsum" // predefined key
        )

        val inviteContent = InviteContent()
        inviteContent.text = "I can't stop playing! Get it here [APP_INVITE_URL]"
        inviteContent.subject = "Check out this app"
        inviteContent.mediaAttachment = MediaAttachment.imageUrl(
            "https://docs.getsocial" +
                    ".im/images/logo.png"
        )
        inviteContent.linkParams = linkParams

        Invites.send(inviteContent, InviteChannelIds.TELEGRAM, {
            "Invites: Success".consolePrint()
        }, {
            "Invites: Canceled".consolePrint()
        }, {
            "Invites: Failure".consolePrint()
        })
    }

    private val customEvent = View.OnClickListener {

        "Custom event: foo->bar".consolePrint()

        try {
            Analytics.trackCustomEvent("lorem_ipsum", mapOf("foo" to "bar"))
        } catch (e: Exception) {
            e.toString().consolePrint()

        }
    }

    private val coupons = View.OnClickListener {
        val code: String = "ABCDE"
        PromoCodes.claim(code, { result: PromoCode ->
            Log.d("PromoCodes", "Promo code: $result")
            "Promo code: $result".consolePrint()
        }, { error: GetSocialError ->
            Log.d("PromoCodes", "Failed to get promo code: $error")
            "Failed to get promo code: $error".consolePrint()
        })
    }

    private val couponsCreate = View.OnClickListener {
        PromoCodes.create(PromoCodeContent.createRandomCode(), { result: PromoCode ->
            Log.d("PromoCodes", "Promo code created: $result")
            "Promo code created: $result".consolePrint()
        }, { error: GetSocialError ->
            Log.d("PromoCodes", "Failed to send notifications: $error")
            "Failed to send notifications: $error".consolePrint()
        })
    }

    private fun String.consolePrint() {
        writeLine("==========")
        writeLine(this)
    }


}
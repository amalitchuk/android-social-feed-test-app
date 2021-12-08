package com.socommtech.getsocial

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.util.Log
import android.view.View
import com.facebook.AccessToken
import com.facebook.AccessTokenTracker
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import im.getsocial.sdk.*
import im.getsocial.sdk.common.PagingQuery
import im.getsocial.sdk.common.PagingResult
import im.getsocial.sdk.communities.*
import im.getsocial.sdk.invites.InviteChannelIds
import im.getsocial.sdk.invites.InviteContent
import im.getsocial.sdk.invites.LinkParams
import im.getsocial.sdk.media.MediaAttachment
import im.getsocial.sdk.promocodes.PromoCode
import im.getsocial.sdk.promocodes.PromoCodeContent
import im.getsocial.sdk.ui.ViewStateListener
import im.getsocial.sdk.ui.communities.ActivityFeedViewBuilder
import im.getsocial.sdk.ui.invites.InvitesViewBuilder
import java.security.MessageDigest

class Features(activity: Activity) {

    val social: View.OnClickListener = View.OnClickListener {
        val query = UsersQuery.find("John")
        val pagingQuery = PagingQuery(query)
        Communities.getUsers(pagingQuery, { result: PagingResult<User> ->
            val users = result.entries
            "Users with name John: $users".consolePrint()
        }, { error: GetSocialError ->
            "Failed to get users: $error".consolePrint()
        })
    }

    val post = View.OnClickListener {
        val query = TopicsQuery.all()
        val pagingQuery = PagingQuery(query)
        Communities.getTopics(pagingQuery, { result: PagingResult<Topic> ->
            val topics = result.entries
            "Topics: $topics".consolePrint()
        }, { error: GetSocialError ->
            "Failed to get list of topics: $error".consolePrint()
        })
    }

    val group = View.OnClickListener {
        val query = GroupsQuery.all()
        val pagingQuery = PagingQuery(query)
        Communities.getGroups(pagingQuery, { result: PagingResult<Group> ->
            val group = result.entries
            "Groups: $group".consolePrint()
        }, { error: GetSocialError ->
            "Failed to get list of groups: $error".consolePrint()
        })
    }

    val groupMember = View.OnClickListener {
        val groupId = "1"
        val query = MembersQuery.ofGroup(groupId)
        val pagingQuery = PagingQuery(query)
        Communities.getGroupMembers(pagingQuery, { result ->
            "Members of group: ${result.entries}".consolePrint()
        }, { error ->
            "Failed to get group members, error: $error".consolePrint()
        })
    }

    val smartInvites: View.OnClickListener = View.OnClickListener {
        val wasShown = InvitesViewBuilder.create()
            .setWindowTitle("Share with Friends!")
            .setViewStateListener(object : ViewStateListener {
                override fun onOpen() {
                    "GetSocial Smart Invites UI was opened".consolePrint()
                }
                override fun onClose() {
                    "GetSocial Smart Invites UI was closed".consolePrint()
                }
            })
            .show()
        "GetSocial Smart Invites UI was shown: $wasShown".consolePrint()
    }

    val activityFeed = View.OnClickListener {
        val wasShown = ActivityFeedViewBuilder.create(ActivitiesQuery.everywhere())
            .setUiActionListener { _, pendingAction ->
                pendingAction.proceed()
            }
            .show()
        "GetSocial Activities UI was shown: $wasShown".consolePrint()
    }

    val triggerLink = View.OnClickListener {
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

    val customEvent = View.OnClickListener {
        "Custom event: foo->bar".consolePrint()
        try {
            Analytics.trackCustomEvent("lorem_ipsum", mapOf("foo" to "bar"))
        } catch (e: Exception) {
            e.toString().consolePrint()
        }
    }

    val coupons = View.OnClickListener {
        val code = "ABCDE"
        PromoCodes.claim(code, { result: PromoCode ->
            "Promo code: $result".consolePrint()
        }, { error: GetSocialError ->
            "Failed to get promo code: $error".consolePrint()
        })
    }

    val couponsCreate = View.OnClickListener {
        PromoCodes.create(PromoCodeContent.createRandomCode(), { result: PromoCode ->
            "Promo code created: $result".consolePrint()
        }, { error: GetSocialError ->
            "Failed to send notifications: $error".consolePrint()
        })
    }

    @SuppressLint("PackageManagerGetSignatures")
    val facebookIdentifier = View.OnClickListener {
        try {
            val info = activity.packageManager.getPackageInfo(
                "com.socommtech.getsocial",
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                "KeyHash: ${encodeToString(md.digest(), DEFAULT)}".consolePrint()
            }
        } catch (e: Exception) {
            e.message?.consolePrint()
        }
        FacebookSdk.sdkInitialize(activity)
        val accessTokenTracker = object : AccessTokenTracker() {
            override fun onCurrentAccessTokenChanged(
                oldAccessToken: AccessToken,
                currentAccessToken: AccessToken
            ) {
                stopTracking() // stop tracking facebook access token changes as we don't need it anymore
                val currentUser = GetSocial.getCurrentUser()
                currentUser.addIdentity(
                    Identity.facebook(currentAccessToken.toString()),
                    {
                        Log.i("Facebook", "success")
                    },
                    {
                        Log.i("Facebook", "conflict")
                    },
                    {
                        Log.i("Facebook", "error")
                    })
            }
        }
        accessTokenTracker.startTracking()
        LoginManager.getInstance().logInWithReadPermissions(
            activity,
            listOf("email", "user_friends")
        )
    }
}
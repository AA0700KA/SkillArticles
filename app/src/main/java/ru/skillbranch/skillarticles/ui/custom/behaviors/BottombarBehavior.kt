package ru.skillbranch.skillarticles.ui.custom.behaviors

import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import kotlinx.android.synthetic.main.layout_bottombar.view.*
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.ui.custom.Bottombar

class BottombarBehavior : CoordinatorLayout.Behavior<Bottombar>() {

    private val TAG = "Behavior"

    private var topbound = 0
    private var bottombound = 0
    private var interceptorEvents = false
    lateinit var dragHelper: ViewDragHelper

    override fun onLayoutChild(parent: CoordinatorLayout, child: Bottombar, layoutDirection: Int): Boolean {
        parent.onLayoutChild(child, layoutDirection)

        if (!::dragHelper.isInitialized) initialize(parent, child)
        if (child.isClose) ViewCompat.offsetTopAndBottom(child, bottombound - topbound)
        return true
    }

    private fun initialize(parent: CoordinatorLayout, child: Bottombar) {
        dragHelper = ViewDragHelper.create(parent, 1f, DragHelperCallback())
        topbound = parent.height - child.height
        bottombound = parent.height - child.minHeight
        val webView = child.findViewById<WebView>(R.id.webview)
        webView.webViewClient = WebViewClient()
        webView.loadUrl("https://www.youtube.com/watch?v=Nw-fvUzgm9g&t=1s")
    }

    override fun onStartNestedScroll(coordinatorLayout: CoordinatorLayout,
                                     child: Bottombar,
                                     directTargetChild: View,
                                     target: View,
                                     axes: Int,
                                     type: Int): Boolean = axes == ViewCompat.SCROLL_AXIS_VERTICAL


    override fun onNestedPreScroll(coordinatorLayout: CoordinatorLayout,
                                   child: Bottombar,
                                   target: View,
                                   dx: Int,
                                   dy: Int,
                                   consumed: IntArray,
                                   type: Int) {

        val offset = MathUtils.clamp(child.translationY + dy, 0f, child.minHeight.toFloat())

        if (child.isClose && offset != child.translationY) {
            child.translationY = offset
            Log.d(TAG, "BottombarBehavior: ${dy} and ${child.translationY}")
        }

        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed, type)
    }

    override fun onInterceptTouchEvent(parent: CoordinatorLayout, child: Bottombar, ev: MotionEvent): Boolean {
        when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> interceptorEvents = parent.isPointInChildBounds(child, ev.x.toInt(), ev.y.toInt())
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> interceptorEvents = false
        }
        return if (interceptorEvents) dragHelper.shouldInterceptTouchEvent(ev)
        else false
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: Bottombar, ev: MotionEvent): Boolean {
        if (::dragHelper.isInitialized) {
            dragHelper.processTouchEvent(ev)
        }
        return true
    }

    inner class DragHelperCallback : ViewDragHelper.Callback() {

        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            return child is Bottombar
        }

        override fun getViewVerticalDragRange(child: View): Int {
            return bottombound - topbound
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return MathUtils.clamp(top, topbound, bottombound)
        }

    }

}
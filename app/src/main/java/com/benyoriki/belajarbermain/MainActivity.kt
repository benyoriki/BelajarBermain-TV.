package com.benyoriki.belajarbermain

import android.net.Uri
import android.net.http.SslError
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.webkit.GeolocationPermissions
import android.webkit.PermissionRequest
import android.webkit.RenderProcessGoneDetail
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

/**
 * Satu layar penuh berisi WebView yang membuka situs BelajarBermain lewat HTTPS.
 * - D-pad / OK diteruskan ke halaman (navigasi fokus ada di tv.js pada situs).
 * - Back: ditawarkan dulu ke halaman (tutup dialog / naik satu tingkat); di Beranda perlu ditekan 2x untuk keluar.
 * - Play/Pause remote → musik latar di halaman.
 */
class MainActivity : ComponentActivity() {

    private lateinit var web: WebView
    private lateinit var loading: View
    private lateinit var errorBox: View
    private lateinit var errorMsg: TextView
    private lateinit var retryBtn: Button

    private var pageFailed = false
    private var lastBackAt = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        web = findViewById(R.id.web)
        loading = findViewById(R.id.loading)
        errorBox = findViewById(R.id.error_box)
        errorMsg = findViewById(R.id.error_msg)
        retryBtn = findViewById(R.id.btn_retry)

        retryBtn.setOnClickListener { reload() }
        findViewById<Button>(R.id.btn_exit).setOnClickListener { finish() }

        configureWebView()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = handleBack()
        })

        web.loadUrl(BuildConfig.START_URL)
    }

    // ---------------------------------------------------------------- WebView

    private fun configureWebView() {
        // Inspeksi lewat chrome://inspect hanya pada build debug
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG)

        web.settings.apply {
            javaScriptEnabled = true            // wajib: seluruh situs adalah aplikasi JavaScript
            domStorageEnabled = true            // localStorage: profil, poin, progres anak
            allowFileAccess = false
            allowContentAccess = false
            mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
            cacheMode = WebSettings.LOAD_DEFAULT  // Service Worker situs yang mengatur cache offline
            mediaPlaybackRequiresUserGesture = true
            setSupportZoom(false)
            builtInZoomControls = false
            displayZoomControls = false
            setGeolocationEnabled(false)
            textZoom = 100
            // Penanda untuk situs: aktifkan Mode TV otomatis (lihat skrip di <head> index.html)
            userAgentString = "$userAgentString BelajarBermainTV/${BuildConfig.VERSION_NAME}"
        }
        web.apply {
            overScrollMode = View.OVER_SCROLL_NEVER
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setBackgroundColor(0xFFE9F8FF.toInt())
            isFocusable = true
            isFocusableInTouchMode = true
        }

        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val uri = request.url
                if (isAllowed(uri)) return false
                // Anak-anak di depan TV: tidak membuka situs lain / aplikasi lain
                Toast.makeText(this@MainActivity, R.string.external_blocked, Toast.LENGTH_SHORT).show()
                return true
            }

            override fun onPageStarted(view: WebView, url: String?, favicon: android.graphics.Bitmap?) {
                pageFailed = false
                errorBox.visibility = View.GONE
                loading.visibility = View.VISIBLE
            }

            override fun onPageFinished(view: WebView, url: String?) {
                if (pageFailed) return
                loading.visibility = View.GONE
                view.requestFocus()
            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {
                if (!request.isForMainFrame) return
                showError(R.string.error_offline)
            }

            override fun onReceivedHttpError(view: WebView, request: WebResourceRequest, errorResponse: WebResourceResponse) {
                if (request.isForMainFrame && errorResponse.statusCode >= 400) showError(R.string.error_server)
            }

            override fun onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
                handler.cancel()            // JANGAN PERNAH handler.proceed(): sertifikat tidak valid = tidak dibuka
                showError(R.string.error_ssl)
            }

            override fun onRenderProcessGone(view: WebView, detail: RenderProcessGoneDetail): Boolean {
                // Proses render dimatikan sistem (RAM TV terbatas): bangun ulang layar ini
                recreate()
                return true
            }
        }

        web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) = request.deny()
            override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) =
                callback.invoke(origin, false, false)
        }
    }

    /** Hanya HTTPS ke host yang didaftarkan di build.gradle.kts (ALLOWED_HOSTS). */
    private fun isAllowed(uri: Uri): Boolean =
        uri.scheme == "https" && BuildConfig.ALLOWED_HOSTS.any { uri.host.equals(it, ignoreCase = true) }

    /** Halaman utama aplikasi = bukan "Kisah3Dino" (cerita di situs lain pada host github.io). */
    private fun isOnHome(url: String?): Boolean {
        if (url == null) return true
        val u = Uri.parse(url)
        return !(u.host.equals("benyoriki.github.io", true) && u.path?.startsWith("/BelajarBermain", true) != true)
    }

    private fun showError(msg: Int) {
        pageFailed = true
        loading.visibility = View.GONE
        errorMsg.setText(msg)
        errorBox.visibility = View.VISIBLE
        retryBtn.requestFocus()
    }

    private fun reload() {
        errorBox.visibility = View.GONE
        loading.visibility = View.VISIBLE
        pageFailed = false
        // Bila halaman sebelumnya gagal total, URL awal dimuat ulang; bila tidak, muat ulang halaman saat ini
        val current = web.url
        if (current.isNullOrEmpty() || current.startsWith("about:") || current.startsWith("data:")) {
            web.loadUrl(BuildConfig.START_URL)
        } else {
            web.reload()
        }
    }

    // ---------------------------------------------------------------- Tombol remote

    private fun handleBack() {
        if (errorBox.visibility == View.VISIBLE) {
            confirmExit()
            return
        }
        web.evaluateJavascript(
            "(function(){try{return !!(window.__tvBack&&window.__tvBack())}catch(e){return false}})()"
        ) { consumed ->
            when {
                consumed == "true" -> Unit                                   // halaman memakai tombol Back
                !isOnHome(web.url) -> web.loadUrl(BuildConfig.START_URL)     // dari cerita → kembali ke BelajarBermain
                else -> confirmExit()                                        // di Beranda: tekan 2x untuk keluar
            }
        }
    }

    private fun confirmExit() {
        val now = SystemClock.elapsedRealtime()
        if (now - lastBackAt < 2000) {
            finish()
        } else {
            lastBackAt = now
            Toast.makeText(this, R.string.press_back_again, Toast.LENGTH_SHORT).show()
        }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        when (event.keyCode) {
            KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE, KeyEvent.KEYCODE_MEDIA_PLAY, KeyEvent.KEYCODE_MEDIA_PAUSE -> {
                if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                    web.evaluateJavascript("window.__tvMedia&&window.__tvMedia()", null)
                }
                return true
            }
        }
        return super.dispatchKeyEvent(event)
    }

    // ---------------------------------------------------------------- Siklus hidup

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            WindowInsetsControllerCompat(window, window.decorView).apply {
                hide(WindowInsetsCompat.Type.systemBars())
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        web.onResume()
        web.resumeTimers()
    }

    override fun onPause() {
        web.onPause()
        web.pauseTimers()
        super.onPause()
    }

    override fun onDestroy() {
        (web.parent as? ViewGroup)?.removeView(web)
        web.stopLoading()
        web.webViewClient = WebViewClient()
        web.removeAllViews()
        web.destroy()
        super.onDestroy()
    }
}

package com.example.Example2;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;


import com.DVL.DVL;
import com.sap.ve.DVLClient;
import com.sap.ve.DVLCore;
import com.sap.ve.DVLRenderer;
import com.sap.ve.DVLScene;
import com.sap.ve.DVLTypes.DVLRENDEROPTION;
import com.sap.ve.DVLTypes.DVLRENDEROPTIONF;
import com.sap.ve.DVLTypes.DVLRESULT;
import com.sap.ve.SDVLMatrix;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class Surface extends GLSurfaceView
{
	private DVLCore m_core;
	private GestureHandler m_gestures;
	private CustomRenderer custom_renderder;

	public Surface(Context context) {
		super(context);
		init();
	}

	public Surface(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CustomRenderer getCustomRenderder(){
		return custom_renderder;
	}

	void init() {
		m_core = ((MainActivity) getContext()).getCore();
		m_gestures = new GestureHandler();

		setEGLContextFactory(new ContextFactory());
		setEGLConfigChooser(new ConfigChooser());
		custom_renderder = new CustomRenderer(getContext(), m_core, m_gestures);
		setRenderer(custom_renderder);
		setOnTouchListener(m_gestures);


	}

	private static class ConfigChooser implements GLSurfaceView.EGLConfigChooser
	{
		private static int EGL_OPENGL_ES2_BIT = 4;
		private static int[] s_configAttribs2 =
			{
			EGL10.EGL_RED_SIZE, 5,//minimum is 5 bits per component  (8 bits preferred)
			EGL10.EGL_GREEN_SIZE, 5,//minimum is 5 bits per component (8 bits preferred)
			EGL10.EGL_BLUE_SIZE, 5,//minimum is 5 bits per component (8 bits preferred)
			EGL10.EGL_DEPTH_SIZE, 16,//minimum 16 bits for Z Buffer (24 bits preferred)
			EGL10.EGL_RENDERABLE_TYPE, EGL_OPENGL_ES2_BIT,
			EGL10.EGL_NONE
			};
		private int[] mValue = new int[1];

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display)
		{
			int[] num_config = new int[1];
			egl.eglChooseConfig(display, s_configAttribs2, null, 0, num_config);

			int numConfigs = num_config[0];
			if (numConfigs <= 0)
			{
				throw new IllegalArgumentException("No matching OpenGL configurations");
			}

			EGLConfig[] configs = new EGLConfig[numConfigs];
			egl.eglChooseConfig(display, s_configAttribs2, configs, numConfigs, num_config);
			return chooseConfig(egl, display, configs);
		}

		public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display, EGLConfig[] configs)
		{
			EGLConfig best_config = null;
			int best_z = 0, best_s = 64, best_r = 0, best_g = 0, best_b = 0;

			for(EGLConfig config : configs)
			{
				int z = findConfigAttrib(egl, display, config, EGL10.EGL_DEPTH_SIZE, 0);
				int s = findConfigAttrib(egl, display, config, EGL10.EGL_STENCIL_SIZE, 0);
				int r = findConfigAttrib(egl, display, config, EGL10.EGL_RED_SIZE, 0);
				int g = findConfigAttrib(egl, display, config, EGL10.EGL_GREEN_SIZE, 0);
				int b = findConfigAttrib(egl, display, config, EGL10.EGL_BLUE_SIZE, 0);
				//Z buffer bitness has precedence over RGB bitness (because 16 bit z buffer is not good enough for DVL)
				if ((z < best_z) || (s > best_s) || (r < best_r) || (b < best_b) || (g < best_g))
					continue;

				best_z = z;
				best_s = s;
				best_r = r;
				best_g = g;
				best_b = b;
				best_config = config;
			}

			return best_config;
		}

		private int findConfigAttrib(EGL10 egl, EGLDisplay display, EGLConfig config, int attribute, int defaultValue)
		{
			if (egl.eglGetConfigAttrib(display, config, attribute, mValue))
			{
				return mValue[0];
			}

			return defaultValue;
		}
	}
}


class CustomRenderer implements GLSurfaceView.Renderer
{
	private DVLCore m_core;
	private DVLRenderer m_renderer;
	private DVLScene m_scene;
	//private SDVLProceduresInfo m_proceduresInfo;
	//private SDVLPartsListInfo m_partsListInfo;
	private GestureHandler m_gestures;
	private Context m_context;

	/*public SDVLPartsListInfo getPartsListInfo() {
		return m_partsListInfo;
	}*/

	public CustomRenderer(Context context, DVLCore core, GestureHandler gestures)
	{
		m_context = context;
		m_core = core;
		m_gestures = gestures;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config)
	{
		DVLRESULT res = m_core.InitRenderer();
		if (res.Failed())
			return;

		m_renderer = m_core.GetRenderer();

		m_renderer.SetBackgroundColor(50.0f / 255.0f, 50.0f / 255.0f, 50.0f / 255.0f, 1.0f, 1.0f, 1.0f);
		m_renderer.SetOption(DVLRENDEROPTION.SHOW_DEBUG_INFO, false);
		m_renderer.SetOptionF(DVLRENDEROPTIONF.DYNAMIC_LOADING_THRESHOLD, 0.0f);
		m_renderer.SetOption(DVLRENDEROPTION.AMBIENT_OCCLUSION,false);
		m_renderer.SetOption(DVLRENDEROPTION.HALF_RESOLUTION,true);
		//m_renderer.SetOption(DVLRENDEROPTION.SHOW_BACKFACING,true);
		m_renderer.SetOptionF(DVLRENDEROPTIONF.VIDEO_MEMORY_SIZE,1024.0f);

		m_scene = new DVLScene(0, m_context);
		res = m_core.LoadScene("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/RFRL-Test 2.vds", null, m_scene);
		if (res.Failed()){
			Log.e("VDS","Failed to load ");
			return;
		} else {
			Log.i("VDS","Loaded Successfully !!!!!");
		}

		m_renderer.AttachScene(m_scene);

		DVLClient.setContext(m_context);
		DVL.getInstance().init(m_core);

	}



	public void onSurfaceChanged(GL10 gl, int w, int h)
	{
		m_renderer.SetDimensions(w, h);
	}

	public void onDrawFrame(GL10 gl)
	{
		m_gestures.update(m_renderer);

		SDVLMatrix matView = new SDVLMatrix();
		SDVLMatrix matProj = new SDVLMatrix();
		m_renderer.GetCameraMatrices(matView, matProj);
		m_renderer.RenderFrame();
	}

}


class ContextFactory implements GLSurfaceView.EGLContextFactory
{
	private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

	public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig)
	{
		int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE };
		EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
		return context;
	}

	public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context)
	{
		egl.eglDestroyContext(display, context);
	}
}


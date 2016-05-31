package com.example.Example2;

import com.sap.ve.*;
import com.sap.ve.DVLTypes.*;
import com.example.Example2.GestureHandler;

import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.content.Context;
import android.util.AttributeSet;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

public class Surface extends GLSurfaceView
{
	private DVLCore m_core;
	private GestureHandler m_gestures;

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

		private int[] mValue = new int[1];
	}

	void init()
	{
		m_core = ((MainActivity) getContext()).getCore();
		m_gestures = new GestureHandler();

		setEGLContextFactory(new ContextFactory());
		setEGLConfigChooser(new ConfigChooser());
		setRenderer(new CustomRenderer(getContext(), m_core, m_gestures));

		setOnTouchListener(m_gestures);
	}

	public Surface(Context context)
	{
		super(context);
		init();
	}

	public Surface(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		init();
	}
}



class CustomRenderer implements GLSurfaceView.Renderer
{
	private DVLCore m_core;
	private DVLRenderer m_renderer;
	private DVLScene m_scene;
	private SDVLProceduresInfo m_proceduresInfo;
	private SDVLPartsListInfo m_partsListInfo;
	private GestureHandler m_gestures;
	private Context m_context;

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
		m_renderer.SetOption(DVLRENDEROPTION.SHOW_DEBUG_INFO, true);
		m_renderer.SetOption(DVLRENDEROPTION.HALF_RESOLUTION, true);

		m_scene = new DVLScene(0, m_context);
		res = m_core.LoadScene("file://" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/VDS/file1.vds", null, m_scene);
		if (res.Failed())
			return;

		m_renderer.AttachScene(m_scene);

		m_proceduresInfo = new SDVLProceduresInfo();
		m_scene.RetrieveProcedures(m_proceduresInfo);
		//m_scene.ActivateStep(m_proceduresInfo.portfolios.get(0).steps.get(0).id, true, true);

		m_partsListInfo = new SDVLPartsListInfo();
		m_scene.BuildPartsList(DVLPARTSLIST.RECOMMENDED_uMaxParts, DVLPARTSLIST.RECOMMENDED_uMaxNodesInSinglePart, DVLPARTSLIST.RECOMMENDED_uMaxPartNameLength,
				DVLPARTSLISTTYPE.ALL, DVLPARTSLISTSORT.NAME_ASCENDING, DVLTypes.DVLID_INVALID, "", m_partsListInfo);
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

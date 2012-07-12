
/*
	dsj demo application

	© Copyright np 2006/7, humatic gmbh, berlin, germany. All rights reserved.

	This software is supplied to you by humatic
	("humatic") in consideration of your agreement to the following terms, and your
	use, installation, modification or redistribution of this software
	constitutes acceptance of these terms.  If you do not agree with these terms,
	please do not use, install, modify or redistribute this software.

	In consideration of your agreement to abide by the following terms, and subject
	to these terms, humatic grants you a personal, non-exclusive license, under humatic's
	copyrights in this original humatic software (the "humatic software"), to use,
	reproduce, modify and redistribute the humatic software, with or without
	modifications, in source and/or binary forms; provided that if you redistribute
	the humatic software in its entirety and without modifications, you must retain
	this notice and the following text and disclaimers in all such redistributions of
	the software.  Neither the name, trademarks, service marks or logos of
	humatic may be used to endorse or promote products derived from the
	software without specific prior written permission from humatic.  Except as
	expressly stated in this notice, no other rights or licenses, express or implied,
	are granted by humatic herein, including but not limited to any patent rights that
	may be infringed by your derivative works or by other works in which the humatic
	software may be incorporated.

	The software is provided by humatic on an "AS IS" basis.  HUMATIC MAKES NO
	WARRANTIES, EXPRESS OR IMPLIED, INCLUDING WITHOUT LIMITATION THE IMPLIED
	WARRANTIES OF NON-INFRINGEMENT, MERCHANTABILITY AND FITNESS FOR A PARTICULAR
	PURPOSE, REGARDING THE HUMATIC SOFTWARE OR ITS USE AND OPERATION ALONE OR IN
	COMBINATION WITH YOUR PRODUCTS.

	IN NO EVENT SHALL HUMATIC BE LIABLE FOR ANY SPECIAL, INDIRECT, INCIDENTAL OR
	CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE
	GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
	ARISING IN ANY WAY OUT OF THE USE, REPRODUCTION, MODIFICATION AND/OR DISTRIBUTION
	OF THE HUMATIC SOFTWARE, HOWEVER CAUSED AND WHETHER UNDER THEORY OF CONTRACT, TORT
	(INCLUDING NEGLIGENCE), STRICT LIABILITY OR OTHERWISE, EVEN IF HUMATIC HAS BEEN
	ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/


import de.humatic.dsj.*;

import de.humatic.dsj.sbe.*;
import de.humatic.dsj.sink.*;
import de.humatic.dsj.rc.*;
import de.humatic.dsj.ext.Registry.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.awt.geom.*;
import javax.swing.*;

import javax.sound.sampled.*;
import java.io.*;


public class DSJDemo extends Frame implements ActionListener, java.beans.PropertyChangeListener{


	public static void main(String[] _args) {

		new DSJDemo();

	}

	private DSFiltergraph dsfg;

	private DirectDisplay dd;

	private boolean running = true,
					newFrameArrived,
					showRendererCtrls,
					audioOnly;

	private MenuBar menuBar;

	private Menu dvMenu,
				 capMenu,
				 sink,
				 stream,
				 edit;

	private MenuItem open,
				  	 openURL,
				  	 openDVD,
				  	 openGraph,
				  	 openDV,
				  	 openCap,
				  	 buildBDA,
				  	 buildSBG,
				  	 buildHDV,
				  	 setFile,
				  	 capPreview,
				  	 goFull,
				  	 encode,
				  	 loop,
				  	 lock,
				  	 meta,
				  	 preview,
				  	 devCtrl,
					 d2d,
					 d2f,
					 f2d,
					 setRes,
					 showList,
					 showGraph,
					 manual,
					 javaSrc,
					 multi,
					 frameGrabber;

	private String[] sinks = new String[]{"mp4", "ogg", "matroska", "mp2", "lead network"},
					 networkSinks = new String[]{"WindowsMedia", "Lead (ltsp)"},
					 edits = new String[]{"set editable", "cut", "trim", "copy", "paste", "replace", "add", "save"};

	private int w,
				h,
				renderingMode,
				frameAnim;

	private Image logo;

	private SwingMovieController smc;

	private DSFilterInfo[][] dsi;

	private DSCapture left,
					  right;

	private JAudioPlayer jap;

	private DSMovie.MovieSelection copiedSelection;

	private JavaOverlayFilter jof;

	/**

	This code makes use of the dsj.xml setup file located next to it. If you copy the code or jar files to run them
	from some other place, copy the xml file along with it.

	**/

	public DSJDemo() {

		super("dsj - DirectShow <> Java");

		try {  UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); }catch (Exception e){}

		setLayout(new BorderLayout());

		setBackground(Color.black);

		System.out.println(DSEnvironment.getVersionInfo());

		menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");

		open = new MenuItem("open file");

		open.addActionListener(this);

		fileMenu.add(open);

		openURL = new MenuItem("open url");

		openURL.addActionListener(this);

		fileMenu.add(openURL);

		openDVD = new MenuItem("open dvd");

		openDVD.addActionListener(this);

		fileMenu.add(openDVD);

		openGraph = new MenuItem("open .grf or .xgr file");

		openGraph.addActionListener(this);

		fileMenu.add(openGraph);

		openDV = new MenuItem("open DV device");

		openDV.addActionListener(this);

		fileMenu.add(openDV);

		openCap = new MenuItem("open capture device");

		openCap.addActionListener(this);

		fileMenu.add(openCap);

		buildBDA = new MenuItem("build BDA graph");

		buildBDA.addActionListener(this);

		fileMenu.add(buildBDA);

		buildSBG = new MenuItem("build stream buffer graph");

		buildSBG.addActionListener(this);

		fileMenu.add(buildSBG);

		buildHDV = new MenuItem("open HDV device");

		buildHDV.addActionListener(this);

		fileMenu.add(buildHDV);

		fileMenu.addSeparator();

		manual = new MenuItem("build graph in code");

		manual.addActionListener(this);

		fileMenu.add(manual);

		javaSrc = new MenuItem("build java source graph");

		javaSrc.addActionListener(this);

		fileMenu.add(javaSrc);

		multi = new MenuItem("open 2 capture devices");

		multi.addActionListener(this);

		fileMenu.add(multi);

		frameGrabber = new MenuItem("framegrabber");

		frameGrabber.addActionListener(this);

		fileMenu.add(frameGrabber);

		menuBar.add(fileMenu);

		Menu optMenu = new Menu("Options");

		goFull = new MenuItem("Fullscreen");

		goFull.addActionListener(this);

		optMenu.add(goFull);

		optMenu.addSeparator();

		edit = new Menu("Edit");

		for (int i = 0; i < 8; i++) {

			final MenuItem emi = new MenuItem(edits[i]);

			emi.addActionListener(this);

			emi.setEnabled(i == 0 || i == 3);

			edit.add(emi);

		}

		optMenu.add(edit);

		optMenu.addSeparator();

		encode = new MenuItem("Export");

		encode.addActionListener(this);

		optMenu.add(encode);

		sink = new Menu("Connect file sink");

		for (int i = 0; i < 5; i++) {

			final MenuItem smi = new MenuItem(sinks[i]);

			smi.addActionListener(this);

			sink.add(smi);

		}

		optMenu.add(sink);

		stream = new Menu("Stream");

		for (int i = 0; i < networkSinks.length; i++) {

			final MenuItem nsmi = new MenuItem(networkSinks[i]);

			nsmi.addActionListener(this);

			stream.add(nsmi);

		}

		optMenu.add(stream);

		optMenu.addSeparator();

		loop = new MenuItem("toggle loop");

		loop.addActionListener(this);

		optMenu.add(loop);

		lock = new MenuItem("toggle aspect lock");

		lock.addActionListener(this);

		optMenu.add(lock);

		meta = new MenuItem("get meta data");

		meta.addActionListener(this);

		optMenu.add(meta);

		menuBar.add(optMenu);

		dvMenu = new Menu("(H)DV");

		dvMenu.setEnabled(false);

		preview = new MenuItem("preview");

		preview.addActionListener(this);

		dvMenu.add(preview);

		d2d = new MenuItem("direct to disc / TS capture");

		d2d.addActionListener(this);

		dvMenu.add(d2d);

		d2f = new MenuItem("grab tape to file / capture ES");

		d2f.addActionListener(this);

		dvMenu.add(d2f);

		f2d = new MenuItem("write DV-file to tape");

		f2d.addActionListener(this);

		dvMenu.add(f2d);

		setRes = new MenuItem("set DV resolution");

		setRes.addActionListener(this);

		dvMenu.addSeparator();

		dvMenu.add(setRes);

		menuBar.add(dvMenu);

		capMenu = new Menu("Capture");

		capMenu.setEnabled(false);

		menuBar.add(capMenu);

		Menu envMenu = new Menu("Environment");

		showList = new MenuItem("Filter listing");

		showList.addActionListener(this);

		envMenu.add(showList);

		showGraph = new MenuItem("List filters in graph");

		showGraph.addActionListener(this);

		envMenu.add(showGraph);

		menuBar.add(envMenu);

		setMenuBar(menuBar);

		w = 320;

		h = 240;

		dd = new DirectDisplay(w, h+10);

		add("Center", dd);

		smc = new SwingMovieController();

		add("South", smc);

		pack();

		setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - w/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - h/2));

		setVisible(true);

		toFront();

		addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent e) {

				running = false;

				try{

					if (jap != null) jap.close();

					if (dsfg != null) {
						dsfg.dispose();
					}

					if (left != null) {
						left.dispose();
						right.dispose();
					}
				}catch (Exception ex){ex.printStackTrace();}
				System.exit(0);
			}

			public void windowClosed (WindowEvent e) {
				System.exit(0);
			}
		});


	}

	public void actionPerformed(ActionEvent e){

		String path = "";

		if (e.getSource() == open) {

			FileDialog FD = new FileDialog(this, "", FileDialog.LOAD);
			FD.setVisible(true);

			path = new File(FD.getDirectory()+File.separator+FD.getFile()).getAbsolutePath();

			if (FD.getFile() == null) return;

			loadMovie(path, getFlags(DSFiltergraph.MOVIE));


		} else if (e.getSource() == openURL) {

			Object[] message = new Object[2];

			final JComboBox fav = new JComboBox(new String[]{"enter or select url", "http://www.dnbradio.com/hi.asx", "http://lsd.newmedia.tiscali-business.com/bb/redirect.lsc?content=live&media=ms&stream=tagesschau/live1high.wmv", "mms://213.155.73.51/del-tv_dsl_broad", "http://127.0.0.1:8080/anyFile.asf", "ltsf://127.0.0.1:27015",  "rtp://233.4.12.3:5005", "http://www.raytheon.com/broadcast/we_are_raytheon.asx", "mms://fjord.nationalgeographic.com/channel/highspeed/2005/03/20050314SecondsChernobyl.asf", "mms://stream1.orf.at/fm4_live", "http://www.editingarchive.com:8000/eatv-public.ogg.m3u", "http://85.17.103.10:8000", "http://www.deadchickens.de/04/lille/vid/tourcoing.wmv", "mms://213.200.64.227/swr3/msmedia/musik/melua/closest_thing.wmv"});
			fav.setEditable(true);

			message[0] = new javax.swing.JLabel("<html>Select or enter url<br>(This will use DSMovie and only works with urls / protocols that DirectShow handles natively.<br>For more streaming options see the de.humatic.dsj.src package)</html>");
			message[1] = fav;

			Object[] options = {"  ok  "};
			int choice = JOptionPane.showOptionDialog(this,
													 message,
													 " ",
													 JOptionPane.DEFAULT_OPTION,
													 JOptionPane.INFORMATION_MESSAGE,
													 null,
													 options,
													 options[0]
											);

			if (fav.getSelectedIndex() != -1) path = fav.getItemAt(fav.getSelectedIndex()).toString().trim();
			else path = fav.getSelectedItem().toString().trim();

			if (path == null) return;

			loadMovie(path, DSFiltergraph.DD7 );

		} else if (e.getSource() == openDVD) {

			loadMovie("dvd", getFlags(DSFiltergraph.DVD));

		} else if (e.getSource() == openGraph) {

			path = showFileDialog("select .grf/.xgr file", FileDialog.LOAD);

			loadMovie(path, getFlags(DSFiltergraph.GRAPH));

			try{ Thread.currentThread().sleep(1000); }catch (Exception ex){}

			pack();

			/**

			DSGraph can also be constructed by passing in raw xml as a String.
			Node and parameter names follow the XGR format. The document element can be named as you like. Remember to unescape quotes and backslashes in paths.
			This example would just read, split, remux and write (i.e. copy) an avi file:

			String rawXML = new String("<dsjGraphXML><FILTER ID=\"File_writer\" clsid=\"{8596E5F0-0DA5-11D0-BD21-00A0C911CE86}\"><PARAM name=\"src\" value=\"dest.avi\"/><PARAM name=\"data\" value=\"3000300030003000300030003000300030003000300020000800000000000000\"/></FILTER><FILTER ID=\"AVI_Mux\" clsid=\"{E2510970-F137-11CE-8B67-00AA00A3F1A6}\"><PARAM name=\"data\" value=\"300030003000300030003000300030003000300030002000200000000000000080969800000000000000000000000000FFFFFFFF01000000\"/></FILTER><FILTER ID=\"AVI_Splitter\" clsid=\"{1B544C20-FD0B-11CE-8C63-00AA0044B51E}\"></FILTER><FILTER ID=\"AVI_Source_0\" clsid=\"{E436EBB5-524F-11CE-9F53-0020AF0BA770}\"><PARAM name=\"src\" value=\"source.avi\"/></FILTER><connect direct=\"yes\" src=\"AVI_Source_0\" srcpin=\"Output\" dest=\"AVI_Splitter\" destpin=\"input pin\"/><connect direct=\"yes\" src=\"AVI_Splitter\" srcpin=\"Stream 00\" dest=\"AVI_Mux\" destpin=\"Input 01\"/><connect direct=\"yes\" src=\"AVI_Splitter\" srcpin=\"Stream 01\" dest=\"AVI_Mux\" destpin=\"Input 02\"/><connect direct=\"yes\" src=\"AVI_Mux\" srcpin=\"AVI Out\" dest=\"File_writer\" destpin=\"in\"/></dsjGraphXML>");

			dsfg = new DSGraph(rawXML, DSFiltergraph.HEADLESS, null);

			**/

		} else if (e.getSource() == openDV) {

			loadMovie("dv", getFlags(DSFiltergraph.DV));

			dvMenu.setEnabled(true);

			capMenu.setEnabled(false);

		} else if (e.getSource() == openCap) {

			if (dsfg != null) {

				running = false;

				remove(dsfg.asComponent());

				dsfg.dispose();

			}

			dvMenu.setEnabled(false);

			capMenu.setEnabled(true);

			Object[] options = {"no ", "yes"};

			int result = JOptionPane.showOptionDialog(this,
														"	Resolve device pins and formats?",
														" 			dsj capture",
														JOptionPane.YES_NO_CANCEL_OPTION,
														JOptionPane.QUESTION_MESSAGE,
														null,
														options,
														options[1]
													  );

			if (dsi == null) dsi = DSCapture.queryDevices(result);

			int[] chosenDevices = showCaptureGraphConfiguration((result & 1) != 0);

			if (chosenDevices[0] == dsi[0].length-1 && chosenDevices[1] == dsi[1].length-1) return;

			dsfg = new DSCapture(getFlags(DSFiltergraph.CAPTURE), dsi[0][chosenDevices[0]], chosenDevices[2] == 1, dsi[1][chosenDevices[1]], this);

			capMenu.removeAll();

			for (int i = 0; i < dsi[0].length-1; i++) {

				final int id = i;

				final Menu mi = new Menu(dsi[0][i].getName());

				capMenu.add(mi);

				MenuItem activate = new MenuItem("Activate");

				activate.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){

						DSCapture.CaptureDevice cd = ((DSCapture)dsfg).activateDevice(dsi[0][id]);

						/* a real world application would need to change the menus here...*/
					}
				});

				mi.add(activate);

				try { activate.setEnabled(!((DSCapture)dsfg).getActiveVideoDevice().getName().equalsIgnoreCase(dsi[0][i].getName())); } catch (Exception ne){}

			}

			final DSCapture.CaptureDevice avd = ((DSCapture)dsfg).getActiveVideoDevice();

			if (avd != null) {

				int dialogs = avd.getSupportedDialogs();

				if ((dialogs & avd.VFW_SOURCE) != 0) {

				MenuItem devSrc = new MenuItem("VFW-Source");

					devSrc.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.VFW_SOURCE);
						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(devSrc);

				}

				if ((dialogs & avd.VFW_FORMAT) != 0) {

					MenuItem vfwFormats = new MenuItem("VFW-Format");

					vfwFormats.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.VFW_FORMAT);

						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(vfwFormats);

				}

				if ((dialogs & avd.WDM_DEVICE) != 0) {

					MenuItem devProps = new MenuItem("WDM-Device properties");

					devProps.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.WDM_DEVICE);
						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(devProps);

				}

				if ((dialogs & avd.WDM_CAPTURE) != 0) {

					MenuItem formats = new MenuItem("WDM-Capture format control");

					formats.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.WDM_CAPTURE);
						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(formats);

				}

				if ((dialogs & avd.WDM_PREVIEW) != 0) {

					MenuItem pformats = new MenuItem("WDM-Preview format control");

					pformats.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.WDM_PREVIEW);
						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(pformats);

				}

				if ((dialogs & avd.CROSSBAR_1) != 0) {

					MenuItem cb = new MenuItem("Crossbar control");

					cb.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.CROSSBAR_1);
						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(cb);

				}

				if ((dialogs & avd.TV_VIDEO) != 0) {

					MenuItem tv = new MenuItem("TV tuner video");

					tv.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.TV_VIDEO);
						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(tv);

				}

				if ((dialogs & avd.TV_VIDEO) != 0) {

					MenuItem tva = new MenuItem("TV tuner audio");

					tva.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							avd.showDialog(DSCapture.CaptureDevice.TV_AUDIO);
						}
					});

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(tva);

				}

				/** programatical input selection **/

				if (avd.hasCrossBar()) {

					Menu inputSelect = new Menu("Input selection");

					for (int i = 0; i < avd.getCrossBar().getVideoInputs().length; i++) {

						final int id = i;

						final MenuItem route = new MenuItem(avd.getCrossBar().getVideoInputs()[i].getName());

						route.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								avd.connectCrossBarPins(id, 0, true);
							}
						});

						inputSelect.add(route);

					}

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(inputSelect);

				}

				/** programatical TV tuning **/

				if (avd.hasTVTuner()) {

					Menu TVChannel = new Menu("Set TV Channel");

					/** can be hundreds. Don't need hundreds of MenuItems here right now **/

					int max = avd.getTVTuner().getMaxChannel() > 24 ? 24 : avd.getTVTuner().getMaxChannel();

					for (int i = avd.getTVTuner().getMinChannel(); i < max; i++) {

						final int id = i;

						final MenuItem ch = new MenuItem("Channel: "+id);

						ch.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								avd.setTVChannel(id, true);
							}
						});

						TVChannel.add(ch);

					}

					((Menu)capMenu.getItem(((DSCapture)dsfg).getActiveDeviceIndices()[0])).add(TVChannel);

				}



			}

			capMenu.addSeparator();

			for (int i = 0; i < dsi[1].length-1; i++) {

				final int id = i;

				final Menu mi = new Menu(dsi[1][i].getName());

				capMenu.add(mi);

				MenuItem activate = new MenuItem("activate");

					activate.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){

							DSCapture.CaptureDevice cd = ((DSCapture)dsfg).activateDevice(dsi[1][id]);

						}
					});

				mi.add(activate);

				try{ activate.setEnabled(!((DSCapture)dsfg).getActiveAudioDevice().getName().equalsIgnoreCase(dsi[1][i].getName())); } catch (Exception ne){}

			}

			final DSCapture.CaptureDevice aad = ((DSCapture)dsfg).getActiveAudioDevice();

			if (aad != null) {

					int dialogs = aad.getSupportedDialogs();

					if ((dialogs & aad.WDM_DEVICE) != 0) {

						MenuItem devProps = new MenuItem("Device properties");

						devProps.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e){
								aad.showDialog(DSCapture.CaptureDevice.WDM_DEVICE);
							}
						});

						((Menu)capMenu.getItem(dsi[0].length+((DSCapture)dsfg).getActiveDeviceIndices()[1])).add(devProps);

					}

			}

			capMenu.addSeparator();

			setFile = new MenuItem("Set capture file");

			setFile.addActionListener(this);

			capMenu.add(setFile);

			capPreview = new MenuItem("Preview");

			capPreview.addActionListener(this);

			capMenu.add(capPreview);

			devCtrl = new MenuItem("Device Control");

			devCtrl.addActionListener(this);

			capMenu.add(devCtrl);

			initDisplay();

			DSFiltergraph.DSAudioStream das = dsfg.getAudioStream();

			if (das != null) {

				jap = new JAudioPlayer(das);

				jap.start();

			}

		} else if (e.getSource() == buildBDA) {

			cleanup();

			Object[] options = {"DVBT","DVBS"};

			int networkType = JOptionPane.showOptionDialog(this, "         select network type ",
														" dsj bda",
														JOptionPane.YES_NO_CANCEL_OPTION,
														JOptionPane.QUESTION_MESSAGE,
														null,
														options,
														options[0]
													  );
			dsfg = new DSBDAGraph(networkType, getFlags(DSFiltergraph.BDA), this);

			initDisplay();

			showChannelList((DSBDAGraph)dsfg, null, networkType);

		} else if (e.getSource() == buildSBG) {

			buildStreamBufferGraph();

		} else if (e.getSource() == buildHDV) {

			loadMovie("HDVTape", getFlags(DSFiltergraph.HDV));

		} else if (e.getSource() == manual) {

			buildGraphInCode();

		} else if (e.getSource() == javaSrc) {

			buildJavaSrcGraph();

		} else if (e.getSource() == multi) {

			showStereoFrame();

		} else if (e.getSource() == frameGrabber) {

			startFrameGrabber();

		} else if (e.getSource() == showList) {

			showFilterList(false);

		} else if (e.getSource() == showGraph) {

			showFilterList(true);

		}else if (e.getSource() == setFile) {

			dsfg.pause();

			Object[] settings = showExportDialog("test.asf", DSFiltergraph.CAPTURE);

			((DSCapture)dsfg).setCaptureFile((String)settings[0], ((DSFilterInfo)settings[1]), ((DSFilterInfo)settings[2]), ((Boolean)settings[3]).booleanValue());

		} else if (e.getSource() == capPreview) {

			((DSCapture)dsfg).setPreview();

		} else if (e.getSource() == goFull) {

			if (dsfg == null) return;

			GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

			GraphicsDevice[] gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();

			if (gd.length > 1) {

				Object[] options = new String[gd.length];

				for (int i = 0; i < gd.length; i++) options[i] = "Screen "+(i+1);

				int display = JOptionPane.showOptionDialog(this, "         select fullscreen display ",
															" dsj ",
															JOptionPane.YES_NO_CANCEL_OPTION,
															JOptionPane.QUESTION_MESSAGE,
															null,
															options,
															options[0]
							  );

				myDevice = gd[display];

			}

			dsfg.goFullScreen(myDevice, 0);

		} else if (e.getSource() == encode) {

			if (dsfg == null) return;



			Object[] settings = showExportDialog("test.avi", DSFiltergraph.MOVIE);

			((DSMovie)dsfg).export((String)settings[0], (DSFilterInfo)settings[1], (DSFilterInfo)settings[2]);

		} else if (e.getSource() == stream) {

			de.humatic.dsj.sink.WMNetSink wms = new de.humatic.dsj.sink.WMNetSink(dsfg, 8080, null, false);

			dsfg.connectSink(wms);

		} else if (e.getSource() == loop) {

			if (dsfg == null) return;

			dsfg.setLoop(!dsfg.getLoop());

		} else if (e.getSource() == lock) {

			if (dsfg == null) return;

			dsfg.lockAspectRatio(!dsfg.getAspectLocked());

		} else if (e.getSource() == meta) {

			if (dsfg == null || dsfg.type != DSFiltergraph.MOVIE) return;

			String[][] meta = ((DSMovie)dsfg).getMetaData();

			for (int i = 0; i < meta[0].length; i++) System.out.println(meta[0][i]+" - "+meta[1][i]);

		} else if (e.getSource() == preview) {

			if (dsfg == null) return;

			if (dsfg instanceof DSDVCam) ((DSDVCam)dsfg).setPreview();

			else if (dsfg instanceof DSHDVTape) ((DSHDVTape)dsfg).setPreview();

		} else if (e.getSource() == devCtrl) {

			JFrame jf = new JFrame("Device control");

			jf.setLayout(new GridLayout(0,1));

			if (((DSCapture)dsfg).getActiveVideoDevice() != null && ((DSCapture)dsfg).getActiveVideoDevice().getControls() != null) {

				for (int i = CaptureDeviceControls.BRIGHTNESS; i < CaptureDeviceControls.FOCUS; i++) try { jf.add(((DSCapture)dsfg).getActiveVideoDevice().getControls().getController(i, 0, true)); }catch (Exception ex){}

				}

			if (((DSCapture)dsfg).getActiveAudioDevice() != null) for (int i = CaptureDeviceControls.MASTER_VOL; i < CaptureDeviceControls.TREBLE; i++) try { jf.add(((DSCapture)dsfg).getActiveAudioDevice().getControls().getController(i, 0, true)); }catch (Exception ex){}

			if (jf.getContentPane().getComponentCount() == 0) return;

			jf.pack();

			jf.setVisible(true);

		} else if (e.getSource() == d2d) {

			if (dsfg == null) return;

			if (dsfg instanceof DSDVCam) {

				if (((DSDVCam)dsfg).getDeviceMode() != DSDVCam.CAMERA) {

					System.out.println("device not in camera mode");

					return;

				}

				Object[] settings = showExportDialog("test.avi", DSFiltergraph.DV);

				((DSDVCam)dsfg).cameraToFile((String)settings[0], ((DSFilterInfo)settings[1]), ((Boolean)settings[3]).booleanValue(), ((Boolean)settings[2]).booleanValue());

			} else if (dsfg instanceof DSHDVTape) {

				((DSHDVTape)dsfg).captureTransportStream("Grabbed.ts", false, 0);

			}

		} else if (e.getSource() == d2f) {

			if (dsfg == null) return;

			if (dsfg instanceof DSDVCam) {

				if (((DSDVCam)dsfg).getDeviceMode() != DSDVCam.VCR) {

					System.out.println("device not in vcr mode");

					return;

				}

				Object[] settings = showExportDialog("test.avi", DSFiltergraph.DV);

				((DSDVCam)dsfg).grabTapeToFile((String)settings[0], ((DSFilterInfo)settings[1]), ((Boolean)settings[3]).booleanValue(), ((Boolean)settings[2]).booleanValue(), 1);

			} else if (dsfg instanceof DSHDVTape) {

				((DSHDVTape)dsfg).captureElementaryStreams("Grabbed.ts", true);

			}

		} else if (e.getSource() == f2d) {

			if (dsfg == null || !(dsfg instanceof DSDVCam)) return;

			if (((DSDVCam)dsfg).getDeviceMode() != DSDVCam.VCR) {

				System.out.println("device not in VCR mode");

				return;

			}

			FileDialog FD = new FileDialog(this, "", FileDialog.LOAD);

			FD.setVisible(true);

			if (FD.getFile() == null) return;

			path = new File(FD.getDirectory()+File.separator+FD.getFile()).getAbsolutePath();

			((DSDVCam)dsfg).writeFileToTape(path, 2);

		} else if (e.getSource() == setRes) {

			if (dsfg == null || !(dsfg instanceof DSDVCam)) return;

			dsfg.asComponent().setSize(((DSDVCam)dsfg).setResolution());

			w = (int)dsfg.getSize().getWidth();

			h = (int)dsfg.getSize().getHeight();

			add("Center", dsfg.asComponent());

			smc.setFiltergraph(dsfg);

			setSize(new Dimension(w+8, h+78));

			setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - w/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - h/2));

			pack();

		} else if (e.getSource() == sink.getItem(0)) {

			FileSink sink = FileSink.forType(FileSink.MP4, "test.mp4");

			sink.setFlags(Sink.SHOW_DIALOGS);

			dsfg.connectSink(sink);

		} else if (e.getSource() == sink.getItem(1)) {

			FileSink sink = FileSink.forType(FileSink.OGG, "test.ogg");

			dsfg.connectSink(sink);

		} else if (e.getSource() == sink.getItem(2)) {

			FileSink sink = FileSink.forType(FileSink.MKV, "test.mkv");

			dsfg.connectSink(sink);

		} else if (e.getSource() == sink.getItem(3)) {

			FileSink sink = de.humatic.dsj.sink.FileSink.fromXML("IS_MPEGSink", "test.mpg");

			sink.setFlags(Sink.SHOW_DIALOGS);

			dsfg.connectSink(sink);

		} else if (e.getSource() == stream.getItem(0)) {

			de.humatic.dsj.sink.WMNetSink wms = new de.humatic.dsj.sink.WMNetSink(dsfg, 8080, null, true);

			dsfg.connectSink(wms);

		} else if (e.getSource() == stream.getItem(1)) {

			NetworkSink sink = de.humatic.dsj.sink.NetworkSink.fromXML("Lead_NetworkSink", "ltsf://127.0.0.1:27015");

			dsfg.connectSink(sink);

		} else if (e.getSource() == edit.getItem(0)) {

			if (dsfg.type != DSFiltergraph.MOVIE) return;

			if (!((DSMovie)dsfg).getEditable()) {

				((DSMovie)dsfg).setEditable(true);

				smc.showEditingControls();

				pack();

			}

			for (int i = 1; i < 8; i++) edit.getItem(i).setEnabled(true);

		} else if (e.getSource() == edit.getItem(1)) {

			((DSMovie)dsfg).cut();

		} else if (e.getSource() == edit.getItem(2)) {

			((DSMovie)dsfg).trim();

		} else if (e.getSource() == edit.getItem(3)) {

			copiedSelection = ((DSMovie)dsfg).copy();

		} else if (e.getSource() == edit.getItem(4)) {

			if (copiedSelection == null) return;

			((DSMovie)dsfg).paste(copiedSelection);

		} else if (e.getSource() == edit.getItem(5)) {

			((DSMovie)dsfg).replace(DSMovie.copyToClipboard(showFileDialog("select source file", FileDialog.LOAD), 1000, 2000));

		} else if (e.getSource() == edit.getItem(6)) {

			((DSMovie)dsfg).add(DSMovie.copyToClipboard(showFileDialog("select source file", FileDialog.LOAD), 3000, 25000));

		} else if (e.getSource() == edit.getItem(7)) {

			try{ ((DSMovie)dsfg).saveAs(showFileDialog("select target file", FileDialog.SAVE), true); } catch (Exception ie){}

		}

	}

	private void loadMovie(String path, int flags) {

		running = false;

		if (dsfg != null) {

			/** This cleans up the native structures and removes the display component from
			any container it was added to. It's not essential to do so anymore as
			dsj >= 0.8 can handle multiple graphs in one process space (see showStereoFrame())
			but we're replacing one with the other here. However all DSFiltergraph subclasses
			should finally be closed with the dispose() method.
			**/

			dsfg.dispose();

			if (jap != null) {

				jap.close();

				jap = null;

			}

		}

		try{

			dsfg = DSFiltergraph.createDSFiltergraph(path, flags, this);

			DSFiltergraph.DSAudioStream das = dsfg.getAudioStream();

			if (das != null) {

				jap = new JAudioPlayer(das);

			}

		} catch (DSJException e) {

			System.out.println("\n"+e.toString()+"  "+e.getErrorCode());

			dsfg = null;

			add("Center", dd);

			pack();

			return;

		}

		initDisplay();

		if (jap != null) jap.start();

	}



	private void initDisplay() {

		try{ remove(dd); } catch (Exception e){}

		System.out.println(dsfg.getInfo());

		try{ add("Center", dsfg.asComponent()); }catch (NullPointerException ne){}

		smc.setFiltergraph(dsfg);

		pack();

		int screenWidth = (int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth());

		int screenHeight = (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight());

		setLocation(screenWidth/2 - getWidth()/2, screenHeight/2 - getHeight()/2);

		w = (int)(dsfg.getMediaDimension().getWidth());

		h = (int)(dsfg.getMediaDimension().getHeight());

		running = true;

		toFront();

		if (showRendererCtrls) showRendererControlsDemo();


	}


	public void propertyChange(java.beans.PropertyChangeEvent pe) { //System.out.println("PC "+pe.getNewValue().toString()+"  "+Thread.currentThread());

		switch(DSJUtils.getEventType(pe)) {

			case DSFiltergraph.ACTIVATING: System.out.print("."); break;

			case DSFiltergraph.DONE:

				System.out.println("done playing");

				if (dsfg.isFullScreen() && !dsfg.getLoop()) dsfg.leaveFullScreen();

				break;

			case DSFiltergraph.LOOP: System.out.println("loop"); break;

			case DSFiltergraph.EXIT_FS:

				/**
				dsj will attempt to reembed the display component to where it was before going fullscreen. If
				you wish to put it somewhere else, do it here.
				For the VMR and eventually other cases it may be necessary to call pack() again here.
				**/

				if ((dsfg.getOutFlags() & DSFiltergraph.VMR_EMBED) != 0) pack();

				break;

			case DSFiltergraph.FORMAT_CHANGED:

				add("Center", dsfg.asComponent());

				smc.repaint();

				pack();

				setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - getHeight()/2));

				break;

			case DSFiltergraph.PLAYLIST_PARSED:

				System.out.println("Parsed ASX:");

				String[] hrefs = (String[])(pe.getOldValue());

				for (int i = 0; i < hrefs.length; i++) System.out.println(hrefs[i]);

				break;

			case DSFiltergraph.FRAME_NOTIFY:

				newFrameArrived = true;

				//System.out.println("got frame for: "+Integer.valueOf(pe.getOldValue().toString()).intValue());

				break;

			case DSFiltergraph.GRAPH_EVENT: /*System.out.println("Graph Event: "+DSConstants.eventToString(DSJUtils.getEventValue_int(pe)));*/ break;

			case DSFiltergraph.GRAPH_ERROR: System.out.println("Graph Error: "+pe.getOldValue().toString()+" (see DSJException error codes or DSConstants)"); break;

			case DSFiltergraph.BDA_SCAN_PROGRESS: System.out.println("Channel scan % done: "+ pe.getOldValue().toString()); break;

			case DSFiltergraph.BDA_SCANNED_FREQ: System.out.println("Scanning: "+ pe.getOldValue().toString()); break;

			case DSFiltergraph.BDA_SIG_REC:

				int[] rec = (int[])(pe.getOldValue());

				System.out.println("strength: "+rec[0]+", quality: "+rec[1]+" present: "+(rec[2] != 0)+" locked: "+(rec[3] != 0));

				break;

			case DSFiltergraph.GRAPH_CHANGED:

				System.out.println("Graph has changed");

				try{

					add("Center", dsfg.asComponent());

					pack();

				}catch (Exception e){}

				break;

			case DSFiltergraph.EPG: System.out.println("EPG received"); break;

		}

	}

	private void cleanup() {

		running = false;

		if (dsfg != null) {

			try{ remove(dsfg.asComponent()); }catch (NullPointerException ne){}

			dsfg.dispose();

		}

		else remove(dd);

	}

	/**
	StreamBufferEngine
	=================================================================================================
	**/

	private void buildStreamBufferGraph() {

		cleanup();

		Object[] options = {" DV/MPEG ","BDA", "File"};

		int sourceType = JOptionPane.showOptionDialog(this, "         select source type ",
													" Stream Buffer Engine",
													JOptionPane.YES_NO_CANCEL_OPTION,
													JOptionPane.QUESTION_MESSAGE,
													null,
													options,
													options[0]
									  );

		/** standard - use MPEG2 or DV devices only **/

		boolean standard = false;

		DSFilterInfo[][] matchingCaptureDevices = DSStreamBufferGraph.getStreamBufferSourceDevices(sourceType, standard);

		StreamBufferCaptureSource capSrc = null;

		StreamBufferBDASource bdaSrc = null;

		StreamBufferFileSource fileSrc = null;

		switch (sourceType) {

			case StreamBufferSource.SB_SRC_CAPTURE:

				if (matchingCaptureDevices[0] == null) {
					System.out.println("no matching capture source found");
					return;
				}

				if (standard && matchingCaptureDevices[0].length > 0) {

					capSrc = (StreamBufferCaptureSource)DSStreamBufferGraph.createSource(StreamBufferSource.SB_SRC_CAPTURE, matchingCaptureDevices[0][0], 0);

					dsfg = new DSStreamBufferGraph(DSFiltergraph.DD7, capSrc, this);

				} else if (!standard && matchingCaptureDevices[0].length > 0 && matchingCaptureDevices[1].length > 0) {

					capSrc = (StreamBufferCaptureSource)DSStreamBufferGraph.createNonStandardSource(StreamBufferSource.SB_SRC_CAPTURE, matchingCaptureDevices[0][0], matchingCaptureDevices[1][0], 0);

					dsfg = new DSStreamBufferGraph(DSFiltergraph.DD7, capSrc, this);

				}

				break;

			case StreamBufferSource.SB_SRC_BDA:

				bdaSrc = (StreamBufferBDASource)DSStreamBufferGraph.createSource(StreamBufferSource.SB_SRC_BDA, matchingCaptureDevices[0][0], DSBDAGraph.DVBT);

				dsfg = new DSStreamBufferGraph(DSFiltergraph.DD7, bdaSrc, this);

				break;

			case StreamBufferSource.SB_SRC_FILE:

				FileDialog fd = new FileDialog(this, "Open MPEG2 or DV file", FileDialog.LOAD);

				fd.setVisible(true);

				fileSrc = (StreamBufferFileSource)DSStreamBufferGraph.createSource(StreamBufferSource.SB_SRC_FILE, DSFilterInfo.createFileInfo(fd.getDirectory()+File.separator+fd.getFile()), 0);

				dsfg = new DSStreamBufferGraph(DSFiltergraph.DD7, fileSrc, this);

				break;

		}

		((DSStreamBufferGraph)dsfg).setCaptureFile("C://myRecording.dvr-ms");

		initDisplay();

		if (capSrc != null && capSrc.getControls() != null) {

			JFrame jf = new JFrame("Device control");

			jf.setLayout(new GridLayout(0,1));

			for (int i = 0; i < 16; i++) try { jf.add(((CaptureDeviceControls)capSrc.getControls()).getController(i, 0, true)); }catch (Exception ex){}

			jf.pack();

			jf.setVisible(true);

		} else if (bdaSrc != null) {

			showChannelList(null, bdaSrc, DSBDAGraph.DVBT);

			bdaSrc.loadLastChannel();

		}




	}

	private void editDVR() {

		de.humatic.dsj.sbe.DVREditingSession dvr = new de.humatic.dsj.sbe.DVREditingSession("C://myEdit.dvr-ms", "C://my1stRecording.dvr-ms");

		dvr.append("C://my1stRecording.dvr-ms", 1000, 4000);

		dvr.append("C://my2ndRecording.dvr-ms", 2000, 5000);

		dvr.closeSession();

	}

	/**
	Manual filtergraph building
	===================================================================================================
	This is a VERY simple demonstration of some of the lower level functionality in DSGraph. It builds a simple
	avi playback graph. When digging deeper into this, you will not get around deepend knowledge of
	DirectShow itself. Make sure to get familiar with GraphEdit and other utilities. The methods DSGraph
	and DSFilter provide are pure wrapper functions. There is no safety net between your code and what happens in
	the dsj dll. Things may seem easy at points and lead to immediate shutdown of your PC at others.
	It is worth considering if what you plan to do can not be done somehow with one of the more
	complex dsj classes. For example DSMovie will successfully open most mediafiles and lots of stream
	formats, while this code is basically only capeable of playing back classic avi without "fancy" compression,
	It will fail on mpg, divX, wmv etc.!
	**/

	private void buildGraphInCode() {

		running = false;

		if (dsfg != null) {

			try{ remove(dsfg.asComponent()); }catch (NullPointerException ne){}

			dsfg.dispose();

		}

		else remove(dd);

		try{

			dsfg = DSGraph.createFilterGraph(DSFiltergraph.DD7, this);

			DSFilterInfo dsi = DSFilterInfo.filterInfoForName("File Source (Async.)");

			DSFilter fileSource = ((DSGraph)dsfg).addFilterToGraph(dsi);

			fileSource.setParameter("src", showFileDialog("select avi file", FileDialog.LOAD));

			DSFilter[] dsf = ((DSGraph)dsfg).listFilters();

			fileSource = dsf[0];

			DSFilter.DSPin sourceOutput = dsf[0].getPin(0, 0);

			boolean oneStop = false;

			/**
			renderPin is one of the "magic" functions in DirectShow. It will use the
			Filtergraph's "Intelligent Connect" functionality to automatically pull in
			all additional filters needed to do what either:
			- DirectShow thinks should be done with the Output Pin passed as an argument
			- you suggest to DirectShow by filters added manually before calling this
			Sounds easy? It is easy. However: It has a
			minimum 50/50 chance to fail - due to missing filters or incompatible
			Mediaformats for example. Also: what DirectShow thinks should be done on the
			Pin must not be what you want to achieve.
			So think about using it twice.
			**/

			if (oneStop) fileSource.renderPin(sourceOutput);

			else {

				DSFilter splitter = ((DSGraph)dsfg).addFilterToGraph(DSFilterInfo.filterInfoForName("AVI Splitter"));

				DSFilter.DSPin splitterInput = splitter.getPin(1, 0);

				boolean success = fileSource.connectDownstream(sourceOutput, splitterInput, true);

				if(!success) {

					System.out.println("Can not connect "+sourceOutput+" & "+splitterInput);

					dsfg.dispose();

					dsfg = null;

					pack();

					return;

				}

				DSFilter.DSPin splitterOutput = splitter.getPin(0, 0);

				DSFilter videoRenderer = ((DSGraph)dsfg).addFilterToGraph(DSFilterInfo.filterInfoForName("Video Renderer"));

				DSFilter.DSPin rendererInput = videoRenderer.getPin(1, 0);

				/**
				setting the boolean parameter to false instructs DirectShow to add any
				necassary intermediate filters between the two pins. In this case this may be a
				decompressor depending on the source file's format and/or a Colorspace Converter.
				**/

				success = splitter.connectDownstream(splitterOutput, rendererInput, false);

			}

			 /**all done **/

			((DSGraph)dsfg).setupComplete();

			initDisplay();

		} catch (Exception e) {

			System.out.println("\n"+e.toString());

			dsfg.dispose();

			add("Center", dd);

			pack();

			return;

		}

	}

	/**
	Java source filter
	=================================================================================================
	**/

	private void buildJavaSrcGraph() {

		if (dsfg != null) dsfg.dispose();

		/*
		The java source filter has its issues...
		When rendering to avi this may drop a lot of frames on preview, depending on the compressor chosen,
		This is not that bad when rendering to asf/wmv but still happens.

		For all kinds of file rendering the way to go here should be:
		setting up the animation
		render it to file in one go without preview, making sure that the redraw rate can keep track with
		the framerate the filter has been initialized with.

		Besides that, the filter has proven to nicely work with DirectShow compatible hardware renderers
		(like Blackmagic Decklink boards).
		*/

		Object[] settings = showExportDialog("sourceTest.asf", DSFiltergraph.GRAPH);

		boolean nativeOut = false;

		if (nativeOut) dsfg = DSGraph.createJavaSourceGraph(DSFiltergraph.HEADLESS, JavaSourceFilter.BLOCKING, 720, 576, 25f, null, DSFilterInfo.doNotRender(), true, this);

		else {

			dsfg = DSGraph.createJavaSourceGraph(DSFiltergraph.DD7, JavaSourceFilter.BLOCKING, 480, 360, 25f, (String)settings[0], (DSFilterInfo)settings[1], true, this);

			initDisplay();



		}

		/* see below, line 1910ff.*/

		new Thread(new JSourceDriver()).start();



	}

	/**
	==================================================================================================
	A number of dialogs reused in multiple parts of the demo.
	**/

	private String showFileDialog(String title, int dt) {

		String dialogPath = null;

		try{

			FileDialog FD = new FileDialog(this, title, dt);

			FD.setVisible(true);

			dialogPath = new File(FD.getDirectory()+File.separator+FD.getFile()).getAbsolutePath();

		}catch (Exception e){}

		return dialogPath;

	}

	private Object[] showExportDialog(String fName, int type) {

		final JDialog exp = new JDialog(this, "dsj - capture / export", true);

		final Object[] settings = new Object[]{fName, null, type == DSFiltergraph.DV ? Boolean.valueOf(true) : null, Boolean.valueOf(true)};

		GridLayout gl = new GridLayout(0,1);

		gl.setVgap(5);

		exp.getContentPane().setLayout(gl);

		exp.getContentPane().add(new JLabel("To write WM enter a wmv or asf file & leave"));

		exp.getContentPane().add(new JLabel("codecs set to none! (settings may be shown)"));

		JPanel dest = new JPanel(new BorderLayout());

		final JTextField tf = new JTextField(fName);
		dest.add("Center", tf);

		JButton browse = new JButton("browse");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				settings[0] = showFileDialog("Set capture/export file", FileDialog.SAVE);
				tf.setText((String)settings[0]);
			}
		});
		dest.add("East", browse);

		exp.getContentPane().add(dest);

		final DSFilterInfo[][] installedEncoders = DSEnvironment.getEncoders();

		final JComboBox comp = new JComboBox();

		for (int i = 0; i < installedEncoders[0].length; i++) comp.addItem(installedEncoders[0][i].getName());

		exp.getContentPane().add(new JLabel("video encoder", SwingConstants.CENTER));

		exp.getContentPane().add(comp);

		final JCheckBox sd = new JCheckBox("Show video-encoder settings", false);

		exp.getContentPane().add(sd);

		final JComboBox acomp = new JComboBox();
		final JComboBox aFormats = new JComboBox();

		if (type != DSFiltergraph.DV) {

			exp.getContentPane().add(new JLabel("audio encoder"));

			for (int i = 0; i < installedEncoders[1].length; i++) acomp.addItem(installedEncoders[1][i].getName());

			acomp.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					aFormats.removeAllItems();
					aFormats.removeAllItems();
					DSMediaType[] cf = DSEnvironment.getAudioEncoderMediaTypes(installedEncoders[1][acomp.getSelectedIndex()], dsfg);
					try{
						for (int i = 0; i < cf.length; i++) {
							aFormats.addItem(cf[i].getDisplayString());
						}
					}catch (Exception ex){
						aFormats.removeAllItems();
					}
					if (aFormats.getItemCount() == 0) {
						aFormats.addItem("formats unknown");
						installedEncoders[1][acomp.getSelectedIndex()].setPreferredFormat(DSFilterInfo.SHOW_USER_DIALOG);
					}
				}
			});

			aFormats.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					installedEncoders[1][acomp.getSelectedIndex()].setPreferredFormat(aFormats.getSelectedIndex());
				}
			});

			exp.getContentPane().add(acomp);

			exp.getContentPane().add(aFormats);

		} else {

			final JCheckBox grabDVAudio = new JCheckBox("grab DV audio", true);
			grabDVAudio.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					settings[2] = Boolean.valueOf(grabDVAudio.isSelected());
				}
			});

			exp.getContentPane().add(grabDVAudio);

		}


		if (type != DSFiltergraph.MOVIE) {

			final JCheckBox preview = new JCheckBox("Preview during capture", true);
			preview.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					settings[3] = Boolean.valueOf(preview.isSelected());
				}
			});

			exp.getContentPane().add(preview);

		}

		JButton go = new JButton("go");
		exp.getContentPane().add(go);

		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				settings[0] = tf.getText().trim();
				exp.setVisible(false);
				exp.dispose();
			}
		});

		exp.pack();

		exp.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - exp.getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - exp.getHeight()/2));

		exp.setVisible(true);

		if (sd.isSelected()) installedEncoders[0][comp.getSelectedIndex()].setPreferredFormat(DSFilterInfo.SHOW_USER_DIALOG);

		try{
			settings[1] = installedEncoders[0][comp.getSelectedIndex()];
			//settings[1] = DSFilterInfo.filterInfoForProfile(new java.io.File("profiles/75_100.prx").getAbsoluteFile()); //
	}catch (Exception e){}
		if (type != DSFiltergraph.DV) settings[2] = installedEncoders[1][acomp.getSelectedIndex()];

		return settings;

	}

	/**
	Rendering mode selection:
	**/

	private int getFlags(final int type) {

		renderingMode = 0;

		final JDialog exp = new JDialog(this, "dsj - setup", true);

		JPanel rm = new JPanel(new GridLayout(0, 1));

		final String[] fNames = new String[]{" DD7", " D3D9", " java - J2D", " java - poll", "java - poll rgb", "headless", "VMR9", "overlay / VMR7", " native - GDI", "NULL", "EVR"};

		final int[] flags = new int[]{DSFiltergraph.DD7, DSFiltergraph.D3D9, DSFiltergraph.J2D, DSFiltergraph.JAVA_POLL, DSFiltergraph.JAVA_POLL_RGB, DSFiltergraph.HEADLESS, DSFiltergraph.VMR9, DSFiltergraph.OVERLAY, DSFiltergraph.NATIVE_FORCE_GDI, DSFiltergraph.NULL, DSFiltergraph.EVR};

		final ButtonGroup bg = new ButtonGroup();

		final JCheckBox rcd = new JCheckBox(" renderer-controls demo", showRendererCtrls);
		final JCheckBox dvd = new JCheckBox(" (DVD) mouse control");

		for (int i = 0; i < 9; i++) {
			final int id = i;
			final JCheckBox fBox = new JCheckBox(fNames[i], i== 0);
			fBox.setFocusPainted(false);
			fBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					renderingMode = renderingMode & 0xFF00;
					if (fBox.isSelected()) renderingMode = renderingMode | flags[id];
					rcd.setEnabled(id == 0 || (renderingMode & (DSFiltergraph.JAVA_AUTODRAW | DSFiltergraph.VMR_EMBED)) != 0);
					dvd.setEnabled(type == DSFiltergraph.DVD || (renderingMode & (DSFiltergraph.OVERLAY)) != 0);
				}
			});
			rm.add(fBox);
			bg.add(fBox);
		}

		final JCheckBox da = new JCheckBox(" deliver audio");
		da.setFocusPainted(false);
		da.setBackground(Color.lightGray);
		da.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (da.isSelected()) renderingMode = renderingMode | DSFiltergraph.DELIVER_AUDIO;
				else renderingMode = renderingMode & (0xFFFF ^ DSFiltergraph.DELIVER_AUDIO);
			}
		});
		rm.add(da);

		final JCheckBox paused = new JCheckBox(" paused");
		paused.setFocusPainted(false);
		paused.setBackground(Color.lightGray);
		paused.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (paused.isSelected()) renderingMode = renderingMode | DSFiltergraph.INIT_PAUSED;
				else renderingMode = renderingMode & (0xFFFF ^ DSFiltergraph.INIT_PAUSED);
			}
		});
		rm.add(paused);

		if (type == DSFiltergraph.DVD || type == DSFiltergraph.MOVIE) {

			dvd.setEnabled(type == DSFiltergraph.DVD);
			dvd.setFocusPainted(false);
			dvd.setBackground(Color.lightGray);
			dvd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					if (dvd.isSelected()) renderingMode = renderingMode | DSFiltergraph.DVD_MENU_ENABLED;
					else renderingMode = renderingMode & (0xFFFF ^ DSFiltergraph.DVD_MENU_ENABLED);
				}
			});
			rm.add(dvd);

		}

		final JCheckBox yuv = new JCheckBox(" YUV");
		yuv.setFocusPainted(false);
		yuv.setBackground(Color.lightGray);
		yuv.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				if (yuv.isSelected()) renderingMode = renderingMode | DSFiltergraph.YUV;
				else renderingMode = renderingMode & (0xFFFF ^ DSFiltergraph.YUV);
			}
		});
		rm.add(yuv);

		rcd.setFocusPainted(false);
		rcd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				showRendererCtrls = rcd.isSelected();
			}
		});
		rm.add(rcd);


		JButton go = new JButton("go");
		rm.add(go);

		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				exp.setVisible(false);
				exp.dispose();
			}
		});

		exp.getContentPane().add(rm);

		exp.pack();

		exp.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - exp.getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - exp.getHeight()/2));

		exp.setVisible(true);

		return renderingMode;

	}

	/**
	Capture device selection:
	**/

	private int[] showCaptureGraphConfiguration(boolean resolved) {

		final JDialog cgc = new JDialog(this, "dsj - capture device configuration", true);

		cgc.getContentPane().setLayout(new BorderLayout());

		JPanel av = new JPanel(new GridLayout(2,1));

		JPanel video = new JPanel(new BorderLayout());

		video.setBorder(new javax.swing.border.TitledBorder("Video"));

		GridLayout gl = new GridLayout(0, 1);

		gl.setVgap(5);

		gl.setHgap(5);

		final JComboBox dev = new JComboBox();

		JPanel vUp = new JPanel(gl);

		vUp.setBorder(new javax.swing.border.EmptyBorder(3,3,3,3));

		vUp.add(dev);

		video.add("North", vUp);

		JPanel vidCfg = new JPanel(new BorderLayout());

		JPanel vidLabels = new JPanel(gl);

		vidLabels.setBorder(new javax.swing.border.EmptyBorder(3,10,3,10));

		vidLabels.add(new JLabel("output pin", SwingConstants.CENTER));

		vidLabels.add(new JLabel("format", SwingConstants.CENTER));

		vidLabels.add(new JLabel("custom", SwingConstants.CENTER));

		vidCfg.add("West", vidLabels);

		JPanel vidCtrls = new JPanel(gl);

		vidCtrls.setBorder(new javax.swing.border.EmptyBorder(3,3,3,3));

		final JComboBox pin = new JComboBox();

		vidCtrls.add(pin);

		final JComboBox format = new JComboBox();

		vidCtrls.add(format);

		final JCheckBox useAudio = new JCheckBox("render DV/MPEG/Crossbar audio");

		vidCtrls.add(useAudio);

		vidCfg.add("Center", vidCtrls);

		video.add("Center", vidCfg);

		av.add(video);

		JPanel audio = new JPanel(new BorderLayout());

		audio.setBorder(new javax.swing.border.TitledBorder("Audio"));

		final JComboBox adev = new JComboBox();

		JPanel aUp = new JPanel(gl);

		aUp.setBorder(new javax.swing.border.EmptyBorder(3,3,3,3));

		aUp.add(adev);

		audio.add("North", aUp);

		JPanel audCfg = new JPanel(new BorderLayout());

		JPanel audLabels = new JPanel(gl);

		audLabels.setBorder(new javax.swing.border.EmptyBorder(3,10,3,10));

		audLabels.add(new JLabel("output pin", SwingConstants.CENTER));

		audLabels.add(new JLabel("format", SwingConstants.CENTER));

		audLabels.add(Box.createRigidArea(new Dimension(10, 10)));

		audCfg.add("West", audLabels);

		JPanel audCtrls = new JPanel(gl);

		audCtrls.setBorder(new javax.swing.border.EmptyBorder(3,3,3,3));

		final JComboBox apin = new JComboBox();

		audCtrls.add(apin);

		final JComboBox aformat = new JComboBox();

		audCtrls.add(aformat);

		audCtrls.add(Box.createRigidArea(new Dimension(10, 10)));

		audCfg.add("Center", audCtrls);

		audio.add("Center", audCfg);

		av.add(audio);

		cgc.getContentPane().add("North", Box.createRigidArea(new Dimension(150, 10)));

		cgc.getContentPane().add("Center", av);

		JPanel lower = new JPanel();

		JButton go = new JButton("open");
		go.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				cgc.setVisible(false);
				cgc.dispose();
			}
		});

		JButton cancel = new JButton("cancel");
		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				dev.setSelectedIndex(dev.getItemCount()-1);
				adev.setSelectedIndex(adev.getItemCount()-1);
				cgc.setVisible(false);
				cgc.dispose();
			}
		});

		lower.add(go);

		lower.add(cancel);

		cgc.getContentPane().add("South", lower);

		for (int i = 0; i < dsi[0].length; i++) dev.addItem(dsi[0][i].getName());

		for (int i = 0; i < dsi[1].length; i++) adev.addItem(dsi[1][i].getName());



		if (resolved) {

			dev.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){

					pin.removeAllItems();

					try{

						boolean streamOut = false;

						for (int i = 0; i < dsi[0][dev.getSelectedIndex()].getDownstreamPins().length; i++) {

							pin.addItem(dsi[0][dev.getSelectedIndex()].getDownstreamPins()[i].getName());

							for (int j = 0; j < dsi[0][dev.getSelectedIndex()].getDownstreamPins()[i].getFormats().length; j++) {

								DSMediaType mf = dsi[0][dev.getSelectedIndex()].getDownstreamPins()[i].getFormats()[j];

								if (mf.getMajorType() == DSMediaType.MT_STREAM || mf.getMajorType() == DSMediaType.MT_INTERLEAVED) {

									streamOut = true;

									break;

								}

							}

						}

						useAudio.setEnabled(streamOut || (dsi[0][dev.getSelectedIndex()].getCrossBarInfo() != null && dsi[0][dev.getSelectedIndex()].getCrossBarInfo().getCrossBarOutputs().length > 1));

					}catch (Exception ae){}
				}
			});

			adev.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){

					apin.removeAllItems();

					try{

						for (int i = 0; i < dsi[1][adev.getSelectedIndex()].getDownstreamPins().length; i++) {

							apin.addItem(dsi[1][adev.getSelectedIndex()].getDownstreamPins()[i].getName());

						}

					}catch (Exception ae){}

				}

			});

			pin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){

					format.removeAllItems();

					try{

						for (int i = 0; i < dsi[0][dev.getSelectedIndex()].getDownstreamPins()[pin.getSelectedIndex()].getFormats().length; i++) {

							DSMediaType mf = dsi[0][dev.getSelectedIndex()].getDownstreamPins()[pin.getSelectedIndex()].getFormats()[i];

							format.addItem(mf.getDisplayString());

						}

						format.setSelectedIndex(dsi[0][dev.getSelectedIndex()].getDownstreamPins()[pin.getSelectedIndex()].getPreferredFormat());

					}catch (Exception ae){}

				}

			});

			apin.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){

					aformat.removeAllItems();

					try{

						for (int i = 0; i < dsi[1][adev.getSelectedIndex()].getDownstreamPins()[apin.getSelectedIndex()].getFormats().length; i++) {

							DSMediaType mf = dsi[1][adev.getSelectedIndex()].getDownstreamPins()[apin.getSelectedIndex()].getFormats()[i];

							aformat.addItem(mf.getDisplayString());

						}

						aformat.setSelectedIndex(dsi[1][adev.getSelectedIndex()].getDownstreamPins()[apin.getSelectedIndex()].getPreferredFormat());

					}catch (Exception ae){}

				}

			});

			format.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){

					try{

						dsi[0][dev.getSelectedIndex()].getDownstreamPins()[pin.getSelectedIndex()].setPreferredFormat(format.getSelectedIndex());

					}catch (Exception ae){}

				}

			});

			aformat.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){

					try{

						dsi[1][adev.getSelectedIndex()].getDownstreamPins()[apin.getSelectedIndex()].setPreferredFormat(aformat.getSelectedIndex());

					}catch (Exception ae){}

				}

			});

			dev.setSelectedIndex(0);
			adev.setSelectedIndex(0);

		}

		useAudio.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				adev.setSelectedIndex(adev.getItemCount()-1);
			}
		});

		cgc.pack();

		cgc.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - cgc.getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - cgc.getHeight()/2));

		/**
		dsj will put devices of all kind that drivers are installed for, but that are not
		available at the moment (most likely USB boxes) into a third array slot on
		DSCapture.queryDevices().
		If you are interested in that kind of information, check for dsi[2]...
		**/

		if (dsi.length > 2) {

			System.out.println("Some devices are not available at present:");

			for (int i = 0; i < dsi[2].length; i++) System.out.println(dsi[2][i].getName());

		}

		cgc.setVisible(true);

		return new int[]{ dev.getSelectedIndex(), adev.getSelectedIndex(), useAudio.isSelected() ? 1 : 0};

	}

	/*
	=============================================================================================
	Filter listing
	*/

	private DSFilterInfo[] filters;

	private void showFilterList(boolean inGraph) {

		if (inGraph && dsfg == null) return;

		final JDialog list = new JDialog(this, inGraph ? "Filters in Graph " : "Installed Filters", true);

		final JList filterList = new JList();

		if (!inGraph) {
			final JComboBox cat = new JComboBox(DSEnvironment.getFilterCategories());
			cat.setSelectedIndex(4);
			cat.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e){
					filters = DSEnvironment.getFilters(cat.getSelectedIndex());

					filterList.setListData(filters);
					list.pack();
				}
			});

			list.add(BorderLayout.NORTH, cat);

		}

		try{

			if (!inGraph) {

				filters = DSEnvironment.getFilters(DSEnvironment.CLSID_LegacyAmFilterCategory);

				filterList.setListData(filters);

			} else {

				final DSFilter[] filtersInGraph = dsfg.listFilters();

				filterList.setListData(filtersInGraph);

				filterList.addMouseListener(new MouseAdapter() {
					public void mouseClicked(MouseEvent me) {
						Point p = new Point(me.getX(), me.getY());
						if (me.getClickCount() == 2) filtersInGraph[filterList.locationToIndex(p)].showPropertiesDialog();
						else filtersInGraph[filterList.locationToIndex(p)].dumpConnections();
					}
				});

			}


		}catch (Exception e){e.printStackTrace();}

		list.add(BorderLayout.CENTER, new javax.swing.JScrollPane(filterList));

		list.pack();
		list.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - list.getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - list.getHeight()/2));

		list.setVisible(true);

	}

	private BDAChannelInfo[] channels;
	private String[] names;

	private void showChannelList(final DSBDAGraph bda, final StreamBufferBDASource bdaSrc, final int networkType) {

		try{

			if (bda != null) channels = bda.getChannelsFromXML(networkType);
			else channels = bdaSrc.getChannelsFromXML(networkType);

			names = new String[channels.length];

			for (int i = 0; i < channels.length; i++) names[i] = channels[i].getProgramName();

			final JList channelList = new JList(names);

			final JDialog cl = new JDialog(this, "dsj - BDA Channel list", false);

			cl.setLayout(new BorderLayout());
			//cl.setMinimumSize(new Dimension(190, 490));
			channelList.setBorder(new javax.swing.border.EmptyBorder(5,5,5,5));

			JPanel northern = new JPanel();
			cl.add("North", northern);

			channelList.addMouseListener(new MouseAdapter() {
				public void mouseClicked(MouseEvent me) {
					Point p = new Point(me.getX(), me.getY());
					try{
						if (bda != null) bda.setChannel(channels[channelList.locationToIndex(p)]);
						else if (bdaSrc != null) bdaSrc.setChannel(channels[channelList.locationToIndex(p)]);
					} catch (Exception e){}
				}
			});

			cl.add("Center", new JScrollPane(channelList));

			JPanel southern = new JPanel();

			if (bda != null) {

				final JCheckBox save = new JCheckBox("save to xml", true);

				JButton scan = new JButton("scan channels");
				scan.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e){

						new Thread(new Scanner(bda, save.isSelected())).start();

					}
				});

				southern.add(scan);
				southern.add(save);

			}

			cl.add("South", southern);
			cl.add("West", new JLabel());
			cl.add("East", new JLabel());
			cl.pack();
			cl.setLocation(20, (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - cl.getHeight()/2));

			cl.setVisible(true);

		} catch (Exception e){System.out.println("Error: Invalid channel list file");}

	}

	/**
	multiple graph sample
	===============================================================================================
	Tries to open 2 video capture sources and renders them into a common frame.
	**/

	private void showStereoFrame() {

		if (dsfg != null) {
			remove(dsfg.asComponent());
			dsfg.dispose();
		}

		JFrame stereoFrame = new JFrame("dsj - multigraph sample");

		stereoFrame.getContentPane().setLayout(new GridLayout(1,2));

		dsi = DSCapture.queryDevices();

		if (dsi[0].length < 2) {

			JOptionPane.showMessageDialog(this, "No 2 Capture devices found!");

			return;

		}

		left = new DSCapture(DSFiltergraph.DD7, dsi[0][0], false, DSFilterInfo.doNotRender(), this);

		stereoFrame.getContentPane().add(left.asComponent());

		right = new DSCapture(DSFiltergraph.DD7, dsi[0][1], false, DSFilterInfo.doNotRender(), null);

		stereoFrame.getContentPane().add(right.asComponent());

		stereoFrame.addWindowListener(new WindowAdapter () {
			public void windowClosing (WindowEvent e) {

				try{

					left.dispose();
					right.dispose();
					left = null;
					right = null;

				}catch (Exception ex){}

			}


		});

		stereoFrame.pack();

		stereoFrame.setLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().getWidth()/2 - stereoFrame.getWidth()/2), (int)(Toolkit.getDefaultToolkit().getScreenSize().getHeight()/2 - stereoFrame.getHeight()/2));

		stereoFrame.setVisible(true);

	}

	/**
	Framegrabber
	================================================================================================
	DSFiltergraph must be run in one of the jawt or java side rendering modes ( < HEADLESS) with the FRAME_CALLBACK flag set
	in the constructor in order to do frame grabbing using this approach (Note: VMR9 will also allow for pulling
	frames from the graph using getImage(), but there will be no callbacks, the returned data will be 32bit ABGR and
	it will be read back from graphics RAM, which is slower).
	Whenever a new frame has been delivered from the dll (as a result of an application calling getImage()
	or, in JAVA_AUTODRAW mode, triggered by the internal rendering chain) a PropertyChangeEvent
	of type FRAME_NOTIFY will be  fired that can be used to trigger data aquisition and processing. In principle no heavyweight
	code should be executed inside the callback itself, so in the real world one should consider to use the callback just
	set a flag for a separate thread doing the work.
	**/

	private BufferedImage bi;

	int count;

	private void startFrameGrabber() {

		new java.io.File("grab").mkdirs();

		loadMovie(showFileDialog("select movie file", FileDialog.LOAD), DSFiltergraph.J2D | DSFiltergraph.FRAME_CALLBACK);

		/**
		Both the BufferedImage and its DataBuffer byte[] may be cached. They will
		remain valid throughout the filtergraph's lifetime. With every new frame
		their content will change and may be directly processed.
		**/

		bi = dsfg.getImage();

	}

	/** This is called from propertyChange(PropertyChangeEvent pe). See above. **/

	private void writeFrame() {

		try{

			/** Only in J2D (aka JAVA_AUTODRAW) mode data will allready "be on the javaside". For all other modes call
			getImage() to access the shared memory.**/

			if ((dsfg.getOutFlags() & DSFiltergraph.J2D) == 0) dsfg.getImage();

			/** stop this after 30 frames for the demo **/

			if (count++ < 30) javax.imageio.ImageIO.write(bi,"JPG",new java.io.File("grab/"+System.currentTimeMillis()+".jpg"));

			else dsfg.stop();

		} catch (Exception e){}

	}




	private class DirectDisplay extends JPanel {

		private int offset;

		private DirectDisplay(int width, int height) {

			super();

			setBackground(Color.black);

			setPreferredSize(new Dimension(width, height));

		}


		public void paint(Graphics g)  {

			paintComponent(g);

			try{

				g.setColor(Color.darkGray.darker());

				g.fillRect(70, 40, 50, 160);

				g.fillRect(200, 40, 50, 160);

				g.fillRect(80, 95, 140, 50);

				g.fillRect(145, 15, 30, 70);

				g.fillRect(145, 155, 30, 70);

				g.setColor(Color.black);

			}catch (Exception e) {e.printStackTrace();}

		}

	}

	/**
	Java audio player
	================================================================================================
	Standard javasound code that will read from the filtergraph's DSAudioStream
	**/

	private class JAudioPlayer extends Thread {

		private AudioInputStream ais;

		private AudioFormat format;

		private SourceDataLine line;

		private int bufferSize;

		private DataLine.Info info;

		private JAudioPlayer(DSFiltergraph.DSAudioStream dsAudio) {

			try{

				format = dsAudio.getFormat();

				System.out.println(format);

				bufferSize = dsAudio.getBufferSize();

				ais = new AudioInputStream(dsAudio, format, -1);

				info = new DataLine.Info(SourceDataLine.class, format);

				if (!AudioSystem.isLineSupported(info)) {
					System.out.println("Line matching " + info + " not supported.");
					return;
				}

			}catch (Exception e){}

		}

		public void run() {


			try {

				line = (SourceDataLine) AudioSystem.getLine(info);

				line.open(format, bufferSize  * format.getFrameSize());

				line.start();


			} catch (LineUnavailableException ex) {

				System.out.println("Unable to open the line: " + ex);

				return;

			}

			try{

				byte[] data = new byte[bufferSize];
				int numBytesRead = 0;
				int written = 0;

				while (running) {

					try {

						if ((numBytesRead = ais.read(data)) == -1) break;

						int numBytesRemaining = numBytesRead;

						while (numBytesRemaining > 0 ) {

							written = line.write(data, 0, numBytesRemaining);

							numBytesRemaining -= written;

						}

					} catch (ArrayIndexOutOfBoundsException ae) {

						/**
						Some capture devices eventually deliver larger buffers than they originally say they would.
						Catch that and reset the data buffer
						**/

						bufferSize = numBytesRead;

						data = new byte[bufferSize];

					} catch (Exception e) {e.printStackTrace();
						System.out.println("Error during playback: " + e);
						break;
					}

				}

				line.stop();
				line.flush();
				line.close();

			}catch (Exception e){}

		}

		private void close() {

			try{

				running = false;

				/**
				Give DirectSound based Javasound some time to shut down. .
				**/

				sleep(500);

				ais.close();

			}catch (Exception e){}

		}

	}

	/**
	Run some stupid animation to demonstrate the java source filter
	**/

	private class JSourceDriver extends Thread {

		private byte[] imgData;

		private Color[] rgb = new Color[]{Color.red, Color.green, Color.blue};

		private Color drawColor = Color.red;

		private Graphics2D jSrcGraphics;

		int rectX = 0,
			index = 1,
			frameIndex;

		JSourceDriver(){

			setPriority(Thread.MIN_PRIORITY);

			jSrcGraphics = ((JavaSourceGraph)dsfg).getDrawingSurface();

			jSrcGraphics.setFont(new Font("Arial", 1, 18));

			jSrcGraphics.setBackground(new Color(0, 0, 0, 0));

		}

		public void run() {

			while(running) {

				jSrcGraphics.setColor(drawColor);

				jSrcGraphics.fillRect(0, 160, rectX+=10, 40);

				jSrcGraphics.setColor(Color.black);

				jSrcGraphics.fillRect(220, 20, 40, 40);

				jSrcGraphics.setColor(drawColor);

				jSrcGraphics.drawString(String.valueOf(frameIndex++), 225, 40);

				jSrcGraphics.setColor(Color.black);

				if (rectX > 480) {

					drawColor = rgb[index++%3];

					jSrcGraphics.clearRect(0, 0, 480, 360);

					rectX = 0;

				}

				jSrcGraphics.drawString("dsj Java SourceFilter", 140, 190);

				((JavaSourceGraph)dsfg).submitFrame();

				try{ sleep(40); } catch (InterruptedException ie){}

			}

		}

	}

	/** Unlinks BDA channel scan from the Event Dispatch Thread **/

	private class Scanner extends Thread {

		private boolean save;

		private DSBDAGraph bda;

		private Scanner(DSBDAGraph BDA, boolean doSave) {

			save = doSave;

			bda = BDA;

		}

		public void run() {

			try{
				sleep(500);
				BDAChannelInfo[] channels = bda.performChannelScan(15);
				if (save) bda.persistToXML(bda.getNetworkType(), channels);
			}catch (Exception e){}

		}

	}

	private class Poller extends Thread {

		private Poller() { }

		public void run() {

			while(dsfg != null) {
				try{
					sleep(40);
					dsfg.getImage();
				}catch (Exception e){}
			}

		}

	}

	private void showRendererControlsDemo() {

		final RendererControls rc = dsfg.getRendererControls();

		if (rc == null) return;

		if (rc.type == RendererControls.VMR) {

			/** show off some capabilities of the VMR_EMBED mode when it has been selected **/

			de.humatic.dsj.rc.VMRControls vmrCtrl = (de.humatic.dsj.rc.VMRControls)rc;

			dsfg.setLoop(true);

			String secondPath = showFileDialog("load movie to mix with first one", FileDialog.LOAD);

			if (secondPath != null) {

				vmrCtrl.addFileSource(secondPath, 1, 0);

				vmrCtrl.setAlpha(1, 0.5f);

			}

			/*

						Connect a capture source:

						DSFilterInfo[] vidCapture = DSEnvironment.getFilters(DSEnvironment.CLSID_VideoInputDeviceCategory);

						vmrCtrl.addFilterSource(vidCapture[1], 2, 0);

						vmrCtrl.setAlpha(2, 0.5f);
			*/

			/*
						Connect a graph (which MUST be setup in HEADLESS | INIT_PAUSED mode:

						DSMovie m = new DSMovie(secondPath, DSFiltergraph.DD7 | DSFiltergraph.INIT_PAUSED, null);

						vmrCtrl.addGraphSource(m, 2, 0);
						vmrCtrl.setAlpha(1, 0.5f);


			*/

		}

		new Thread(new OverlayAnimation()).start();

	}


	private class OverlayAnimation extends Thread{

		private OverlayAnimation(){}

		public void run() {

			RendererControls rc = dsfg.getRendererControls();

			//BufferedImage image = javax.imageio.ImageIO.read(new File("test.png").getAbsoluteFile());

			BufferedImage overlay = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
			Graphics2D g2d = overlay.createGraphics();

			String[] spin = new String[]{"--", " \\", " |", " /"};

			g2d.setBackground(new Color(0, 0, 0, 0));
			g2d.setColor(Color.red.darker());

			g2d.setFont(new Font("Arial", Font.BOLD, 36));

			g2d.drawString("dsj©", w/2-40, h-100);

			rc.setOverlayImage(overlay, null, Color.black, 1);

			g2d.setFont(new Font("Arial", Font.BOLD, 112));

			try {
				sleep(2000);
				int ax = 0;
				int ay = 0;
				int i = 0;
				while(ax < w && dsfg.getActive()) {
					ax+=5;
					ay+=5;
					i++;
					g2d.clearRect(0,0, w, h);
					//g2d.drawString("dsj©", w/2-40, h-100);
					if (ax < w/2) g2d.drawString(spin[i%3], w/2-40, h-100);
					rc.setOverlayImage(overlay, null, Color.black, 1);
					rc.setOutputRect(0, -ax/2, -ay/2, w+ax, h+ax);
					rc.setOutputRect(rc.IMG, 0-ax/2, -ay/2, w+ax, h+ax);
					sleep(100);
				}
				rc.setOverlayImage(null, null, null, 0);

				g2d.clearRect(0, 0, w, h);

				rc.setOverlayImage(overlay, null, Color.black, 1f);

				i = 0;

				while(ax > 0 && dsfg.getActive()) {
					ax-=i;
					ay-=i;
					i++;
					rc.setOverlayImage(overlay, null, Color.black, 1);
					if (ax < 0) break;
					rc.setOutputRect(0, -ax/2, -ay/2, w+ax, h+ax);
					rc.setOutputRect(rc.IMG, 0-ax/2, -ay/2, w+ax, h+ax);
					sleep(100);
				}

				g2d.setColor(Color.blue.darker().darker());

				g2d.setFont(new Font("Arial", 1, 36));

				g2d.clearRect(0,0, w, h);
				g2d.drawString("dsj©", w/2-40, h-100);
				g2d.drawString("humatic, Berlin", w/2-130, h-60);

				rc.setOverlayImage(overlay, null, Color.black, 1);

			} catch (Throwable t) {
				t.printStackTrace();
			}

		}


	}

}
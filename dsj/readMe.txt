Thanks for your interest in dsj!

Please read the change log below for news, changes, issues and general comments!

Steps to get dsj going:

- make sure you have DirectX9 and Windows Media Player 9 or greater installed. For mpeg2, DVD playback
  mpeg capture devices and common mpeg4 media you will need DirectShow compatible decoders. These do not come with Windows or DirectX (at least not before Windows 7).  
  Installing ffdShow will enable playback of most formats to encounter, but more specialized filters may
  be needed at points.

- put dsj.dll into your jre / jdk's bin directory or the System folder (or use command line args to set the libary path).
  The dll will be loaded by code in dsj.jar when needed. You do not need and are not supposed to call System.loadLibrary or System.load(..) yourself. If you need to load the dll from some place not on the java library path, use DSEnvironment.setDLLPath as your first call to dsj.

- put dsj.jar into your jre/lib/ext directory or include it to the classpath by other means.

- go/cd to the demo folder (make sure you have no spaces in the path) and doubleclick dsjDemo.jar 
(or type java -jar dsjDemo.jar DSJDemo)

The demo code shows dsj in solo operation. The api should be open enough to integrate with JMF, Quicktime for Java, GStreamer Java  or other dead or emerging java media solutions. 

Code has been developed under Windows XP, Vista and Seven with jdk versions 1.5 & 1.6. The jar is
fully down-compatible with jdk 1.4.2 (jdk 1.4. users will need to edit some points in the demo code though, if they want to run it. These are in first hand related to setPreferredSize(..) calls on awt components). 

As off dsj 0_8_6 the dll is also available as a 64bit build. 

If you can't get the demo to work, try to compile it (again: make sure dsj.jar is on your
classpath). This should give hints on problems. 

The demo makes use of the dsj.xml setup file located next to it. If you copy the code or jar files to run from some other place, copy the xml file along with them.

For licensing and general use terms please see the dsj_license file in the archive.
Unlock codes for non-commercial use can be retrieved online at 
http://www.humatic.de/htools/dsj/register.htm.
Registering will also enable you to download preliminary builds of the next version while it is in the making.

For commercial licensing please contact Christian Graupner at: cg at humatic dot de

Feedback is welcome! Do not hesitate to contact us if you miss features or have problems 
implementing dsj. Also please check the website for updates regularly. Minor updates
& bugfixes will be made available there without further (email) notification whenever they come to 
life. All announcements are posted on the rss feed at http://www.humatic.de/htools/rss/htools.xml

Send your comments to: 

np at humatic dot de

or post to the dsj newsgroup at 

http://groups.google.com/group/humatic_dsj?hl=en

__________________________________________________________________________________________________

Change log:

b_0_8_62
Tons of bugfixes and completions.
All source classes now use SocketFactory and an equivalent URLConnection factory class, so application code can create connections via Proxies etc.
General overhaul of DVD support, added audio & subtitle stream selection, reads DVD structures from harddrives and images
DSBDAGraph switched to Generic Network Provider on Win7
RendererControls for D3D9 renderer added
new HTTPAudio source and sink classes
ShoutcastSource for mp3/aac+ internet radio
MJPG and RTSPSources may read userdata from video streams.
Generic KSPropertySet related methods added to CaptureDeviceControls
Added RTMPT support
RTPHandler for MPEG transport streams via RTP
WebM FileSink & WebM centric MKVSource & Sink class added

b_0_8_61
Added dll internal YUV to RGB/YUV color space converter
Improved compatibility with Windows 7's build in mpeg decoders (may make installation of ffdShow or eq. decoders obsolete for lots of playback cases. Splitter/Parser filters may still be needed)
Async source supports raw DV, MP4 and Matroska streams (requires pull mode DV parser filter / MPEG4 / Matroska Splitter)
Some pitfalls in RTPSink's mpeg audio frameing code removed, VBR support added
Lots of bugfixes, completions & additional server adaptions in RTSP parsing and RTCP code
Support for http-tunnelled  rtsp
Multiple adaptions to changes in native BDA api on Windows 7
Basic EVR (Enhanced Video Renderer) support in DSMovie, DSDvd & DSBDAGraph. Enables use of DXVA hardware accelerated decoders on Vista and Windows 7. Use DSFiltergraph.EVR flag
Swing component creation pushed to EDT
Added audio panning option
DirectDrawControls support drawing over video in YUV mode (NV12, YV12 & YUY2)

fixed bugs (a.o.):
audio only graphs might have started in muted state
String conversion bug in DSMovie.export(..) target file name fixed.
flipImage would not flip return of getImage
possible crash over uninitialized variable in DVCam lookup code
MJPG Source buffer resizing failure fixed
RTCPSockets falsely bound to wildcard address on Windows 7

b_0_8_6
64bit build available
(almost) all image data transfer between c++ and java now using NIO
support for another approach to resizing/cropping capture formats mainly used with pro grade capture devices
new Source subclasses for FLV / MPEG4 & HTTP streams
new NetworkSinks for streaming out in MPEGTransportstream, RTMP & RTP/RTSP format
TS File & NetworkSources handle multiprogram streams
TSFile & NetworkSources support AAC in TS
Improvements to low bandwidth http stream handling in AsyncSource 
BDA - IP broadcast option
JTransInPlace filter to alter data in the native filtergraph from java
StreamBufferEngine should work on Vista/Win7 w/o admin rights
native timeformat may be switched to frames
supports loading of unregistered filters
J2D renderer respects display aspect ratios
anamorphic resize option for "widescreen" streams 

fixed bugs (a.o.):
RTSP / SDP multicast
possible divide by zero crash with faulty driver implementations in queryDevices fixed
Memory leak in EPG code plumbed

b_0_8_53
Added support for H264 livestreams and custom connection parameters to RTMPSource
TSNetwork and FileSources support AVC in transport streams
Added options to DSMovie and all Source subclasses to specify decoder filters.
Better handling of localized pin and filternames and unicode filepaths
RendererControls overlay graphics can now be of none display size and may change in size and location
Added some more colorspace options to pull original - and potentially not directly renderable - YUV data from filtergraphs and to switch the usual 24bit BGR mode to 32bit ABGR
Improved handling of nonstandard protocol/filetype - filter associations
Better URLConnection error handling in MJPEGNetworkSource
more meaningful error reporting in DSDvd

fixed bugs:
ASX Parser, that was broken on Vista, now functional for Win7/Vista
Fixed occassional color glitches in renderer overlays.
Fullscreen mode would have stopped some networkstreams
Fixed double-free bug that caused crashes with DD7 leaveFullScreen / dispose sequences when display component had not been added to any container before going fullscreen. 

b_0_8_52
Added user authentication support (Basic & Digest) to RTSPSource. Also some tweaks to handling of RTSP live sources (IP Cameras) and improved error handling for not entirely standards compliant sources in RTSP parser code.
Improved resyncing in RTMP receiver code. RTMPSource should also properly handle mp3 streams with padding now. 

fixed bugs:
Error in (so far not internally used) BASE64 encoding method in DSJUtils fixed.
Stupid pointer-check bug that made 1st RTMPSource constructor fail fixed
InitPaused caused wrong volume reset w/ non movie classes

b_0_8_51
RTMP source supports MP4, h264/AAC streams and FLV livestreams (no h264 live streams yet). Also some bugs fixed, that led to problems with servers less forgiving than FMS.
Non DirectShow rendering modes can be switched to YUV mode by setting the YUV bit in the flags parameter
Extended VMR9 mixing flag & deinterlacing support. 
Xml preferences can be given in hex
Supports embedding native filter properties pages into java windows
Performance improvement to DSMovie.getThumbnail 
DSEnvironment.getFilters(..) returns sorted lists
Added support for RTSP redirects to RTSPSource, enabling access to youtube mobile rtsp streams. (Google/YouTube's RTSP servers work with UDP only though and thus will not work through firewalls or via proxies)
Added static method for more detailed specification of MPEG2 video output mediatype to DSBDAGraph (some decoders, incl. versions of ffdShow, fail to connect when asked to figure out the stream dimensions from the stream themselves)
Improved on some unfinished business related to capture device output formats. Present methods on DSCapture.CaptureDevice have been deprecated and substituted by versions that work on
a specific pin to gain more control on devices with separate preview and capture outputs. DSCapture.CaptureDevice has a new helper method to locate those pins. Additionally desired framerates can now be given before graph construction for video devices. See the updated "CaptureFormats" sample.

fixed bugs:
Wrong CLSID with non WDM capture device infos in DSCapture.queryDevices fixed
Missing Playout- & DumpSink classes now present...
DSGraph would eventually ignore DELIVER_AUDIO flag
VMR7 & 9 rendering modes ignored INIT_PAUSED flag, D3D9 & J2D mostly did not properly work w/ INIT_PAUSED
Some fixes in audio data delivery to java in relation to high-level javasound api where app.code does not control read sizes
Non-scaled fullscreen display

b_0_8_5
much extended movie editing api
new DS Source filters to receive compressed data from java and - built on those - an all new streamclient api, enabling dsj to receive and play most mpeg4 RTSP streams, RTMP streams from Flash Media Servers, raw RTP streams, MPEG transport and program streams etc. (This is showcased in a webstartable demo available at http://www.humatic.de/htools/dsj/dsj_jws.htm.) 
JNI Implementation of DirectShow's AsyncReader interface enabling dsj to play movie's from java inputstreams and jar URLs
new DSFiltergraph subclass - DSHDVTape - supporting mpeg HDV cameras / D-VHS decks
registerable frame callback for all rendering modes < HEADLESS. Set the FRAME_CALLBACK flag in the graph constructors.
works with DVB-S
new Direct3D9 based JAWT renderer 
shortened constant names for rendering mode selection (see DSFiltergraph javadocs)
better integration of FileSinks with capture graphs
new Sink subclasses for playing out to hardware and dumping elementary streams
JavaSourceFilter now operating with NIO buffers and capeable of doing partial image redraws.
external helper library to simplify dsj use in applets
flickering display issue with jre6 (jdk 1.6.0_10 final release and beyond) and JAWT renderers solved

b_0_8_48
Fixes a resize problem with the JAWT Renderer caused by changed LayoutManager behavior in java 1.6 

b_0_8_47
DSMovie.loadFile now also works with audio only files
Parameter range problems in CaptureDeviceControls fixed (these alse support mechanical pan/tilt now).
Added asf reindexing method to DSMovie
Overlay mode can now have KeyListeners in fullscreen mode (use MOUSE_ENABLED flag on construction)
memory leak in DSCapture plumbed
Added meaning to more PropertyChangeEvent fields

b_0_8_46
DSCapture now allows to configure every single pin on a capture device separately
Fixed problem with ASF FileSinks and wmv9 prx profiles
Some DirectDraw and Java renderer improvements: intermediate format or size changes introduced by 3rd party scaler filters etc.
should now correctly be reflected in java
Improved support for DMO filters
Support for directly capturing MPEG2 from devices with MPEG outputs.
addFilterToGraph now automatically sets up crossbars for capture devices that need one
Size readout bug with mpeg2 HD streams fixed
Introduces DSMediaType structure (which breaks code that uses DSFilterInfo.DSMediaFormat.getSubType()) 

b_0_8_45
Fixed timecode seeking bug in DSDVCam
Fixed heap corruption error in VMR image grabbing code
Added raw bitmap grabbing method to VMRControls
DSCapture record toggling variable now also reset by setPreview()
Problem with Java Plug-In in IE7/Vista solved
Added some code to take care of fancy AudioRenderer plug-ins
Regained some mms capabilities under Vista 
Bug that could stop a JavaSource graph after some arbitrary time fixed
Fixed DirectDraw resizing problem on Vista
Problems with EPG interfaces on secondary BDA graphs fixed

b_0_8_44
Internally added an additional setup call necessary with some Dazzle mpeg capture boxes
Fixed bug that prevented extraction of a movie's audio to wma
Changed interleaving default for avi export to prevent long audio tail when breaking an export 

b_0_8_43
Some changes to the SampleAccessFilter API
Added the NO_SYNC setup flag, which will turn off the graph clock
Added alpha capabilities to the JavaOverlayFilter
Fixed bug related to FrameDropInfo and DV capture devices
Fixed bug that rendered overlay alpha controls in VMRControls useless.

b_0_8_42
Added more general save & load filter state methods to DSFilter
Fixes a possible devide by zero crash in CaptureDevice.setFrameRate() for devicefilters that miss some very basic interfaces
Adds some missing subclass overrides, that resulted in DSGraph always reporting 0 duration
Adds a method to DSFiltergraph to query for seeking capabilities (like: can we play backwards..?).
getTime now returns msec into recorded file in DSCapture & DSDVCam (latter in Camera mode only) when recording.
Added a one call graph dumping method for debugging purposes.
Some internal changes to the Overlay Filter to address reported problems.
Added a vertical retrace sync option to the DirectDraw Renderer. This eats a bit of cpu & needs to be enabled in the xml setup.
The xml setup file will now be created in $user.home and also routinely searched there (app. directory 1st) when setSetupPath is not called

 
b_0_8_41
Fixed a bug in DSFilterInfo.resolve() which could cause crashes in DSCapture.setPreview()
FileSink can now be set up for asf writing (simply do a "new FileSink("WMFile.asf")")

b_0_8_4
Support for movie editing (cut, trim, paste etc.) - Please - if you are using ffdShow - make sure 
to install the latest release from ffdShow_tryouts as older versions have seeking bugs that will 
make parts of this new functionality fail.
Support for USB 2.0 DV Camcorders and VCRs
Fullscreen api now takes GraphicsDevice argument to support secondary monitors
New protocol preferences enable targeted deployment of custom network sources 
A number of both timing & handling related improvements to the java source filter, which also supports audio now
New dll internal DirectShow filters:
InGraph Overlay Filter - for file renderable drawing over video
Sample-data Access Filter - java.nio based null transform filter that can be inserted anywhere in a filtergraph 
DSBDAGraph  supports delivery and filedumping of MPEG2 Transport Streams from BDA receivers.
BDA class reads "now & next" EPG Data.
Implemented access to some DirectShow interfaces related to video compression and capture device output configuration.
May save and restore codec settings.
Numerous other improvements and bug fixes.
Added a number of small isolated single-feature demos
Set up a news & discussion group at: http://groups.google.com/group/humatic_dsj?hl=en

b_0_8_37
Fixed a bug in a callback method, that caused IncompatibleClassChangeErrors on 1.4 JVMs
Repaint of paused graphs in DirectDraw mode improved

b_0_8_36
Video files whose audio track could not be rendered will no longer fail completely, but will fire a 
GRAPH_ERROR event & play without sound.
Workaround for remote file access problems on VMWare and (partly) Vista introduced.
Thread termination bug in the widely neglected SwingMovieController class fixed.

b_0_8_35
New filtering mode preferences for the VMR (see dsj.xml in the demo folder).
Updated some JMF samplecode and repaired pollRGB mode

b_0_8_34
Variable scope problems with multiple VMR instances fixed
Possible threading issues on disposal removed
Some fixes related to the new play selection api

b_0_8_33
Added methods to DSMovie to only play a selected part out of a longer movie. 
Added some security measures that will try to track opened graphs and hardware devices and 
perform a certain degree of automatic cleanup. It is still highly recommended that application code
calls dispose() on every graph it is done with!
Implemented a workaround to receive mouse events from overlay surfaces (where java alone
can not track them). As this is not free in terms of resource usage, it must be enabled by setting
the DVD_MENU_ENABLED bit in the flags parameter on graph construction. Mouse events will be delivered
via the PropertyChangeListener. See updated demo code.
Fixed a performance problem in RENDER_NATIVE mode, that would become obvious as ocassional studdering
mainly with short loops of higher resolution movies.

b_0_8_32
Some errors in overlay positioning removed
ocassional JAVA_AUTODRAW blackscreen problem fixed
Introduced catch for DirectDraw problem in RemoteDesktop sessions
Plumbed leak in VMR Bitmap overlay code
Fixed GDI mode (is anybody using that?)

b_0_8_31
Fixed bug related to VMR input preferences
removed firing of "done" event at loop-point
Fixed new DirectDraw leak on Win2K

b_0_8_3 Feb 2007
Some new sink modules that can stream the output of any DSFiltergraph in WindowsMedia format as well as transcode it to 
mp4, ogg or Matroska or other user-defineable non avi /asf filetypes.
The Java Source filter is now part of the dll, simplifying installation. No code changes needed.
Reads metadata from WindowsMedia, mp3, mp4, mov, vorbis and matroska files
Reads chapters from mp4, mov and matroska files
Includes simple asx parser
Some changes to export feedback methods to simplify GUI integration.
Included default WindowsMedia profiles for audio or video only exports.
Direct passthrough of Filtergraph events.
Some errors and false concepts in Audio Compression Manager handling code corrected.
DSDVCam class can now be constructed with a DSFilterInfo argument in order to support multiple DV devices.
BDA class will also work with "one filter wonder" DVB-T devices.
New overlay rendering mode (make sure you use the constant fields for setup flags and not their numeric
expressions - some non display related flags got pushed up!)
Fixed bug in StreamBuffer code to find DV and MPEG capture devices.
Fixed freezing problem that occasionaly occured on hyperthreaded CPUs and some Dual Core machines after a couple
of hours.
Some changes related to renderer controls: VMRControls now resides - along with the new DirectDraw- and J2DControls
classes - in the rc subpackage and has some method names changed to go in line with the new classes. All the 
control classes provide functionality to do video scaling / panning / zooming and graphics overlays. 
Tested and at points adapted to work on Windows Vista (also see installation notes above)

b_0_8_23
Fixed problem with multiple native displays, where sometimes one would get lost
Plumbed WindowsMedia memory leak
Extended error catching for strange behaviour of some capture devices when running with
DELIVER_AUDIO bit set. Application code needs to check for a change in array size and
eventually adjust it, see JavaAudioPlayer in the demo.

b_0_8_22
Fixed newly made bug that rendered poll modes useless
Corrected error in DV timecode

b_0_8_21, 
Introduced some logic to provide better long term AV sync on file capture with some devices.
Better internationalization for passed string parameters.
Problem with some older videocodecs that would not render when playing files from a network share fixed.

b_0_8_2, December 2006
New rendering mode that embeds DirectShow's VideoMixingRenderer9 into Java Windows.
Possibility to load WindowsMedia9 advanced and custom profiles into all encoding and file capture
graphs. (See DSFilterInfo.filterInfoForProfile(..))
Native display component may now be "reparented"
Two new DSFiltergraph subclasses:
DSStreamBufferGraph wraps the basic technology behind time shifted recording as known from 
XP Media Center Edition and alternative products. This requires XP SP1 or greater. Along with this 
comes coverage for the high-level DVR-MS editing api.
DSBDAGraph lets dsj build digital TV graphs for devices, whose drivers follow the BDA standard
This is only tested for some DVB-T devices so far...very beta
A number of static methods in de.humatic.dsj.ext.Registry to read and manipulate values in the Windows registry.
Problem with XVid Codec on JVM shutdown fixed.
Native capture device dialogs no longer require the display component to be shown.
Frame grabbing now available in all rendering modes (except HEADLESS).
Long standing problem that would cause enigmatic problems on some PCs with analogue TV boards
during capture device query fixed.
Fixed potential problem with DV to WindowsMedia encoding on machines that have ffdshow installed (This
is done by disabling ffdshow for DV. I basically hate to do things like this, but on the other hand
I think some filter authors should stop making their stuff creep into places where perfect support is 
available from the OS)
Java binaries now fully jdk 1.4 compatible (that's in dsj.jar, NOT in the demo code! Change it yourself
if you need to run 1.4).
All deprecated java api calls removed 
More bugs, memory leakage and stability issues fixed.


b_0_8_13
Fixed bug in URL resolving code, that would let URLs with uppercase letters fail.. 

b_0_8_12
Fixed bug that made DSFilterInfos as returned by DSEnvironment.listFilters() useless
for CaptureDevice creation.. 

b_0_8_11
Fixed bug in DSMovie export code that prevented creation of audio only files. 

b_0_8_1, September 2006:
Fixed some thread safety, memory management and overall stability issues that the native redesign 
had invoked (thanks Ian).
Introduced the DSJException class to better handle abnormal situations and to pass more
information on these to the application.
Recreated some basic graph building code at points to better cope with the standard DirectShow
filter set, i.e. dsj should be "less demanding" in terms of filter requirements.
Added a dll internal DirectShow Filter to fix the conceptual DirectShow issue of "still frame 
recording" when pausing file capture. This will be pulled into the graphs that both DSCapture and
DSDVCam build. It does however not yet work with mpeg devices.
Added access to capture device control interfaces that allow to adjust parameters like brightness
contrast, hue, audio levels etc. (+ pan, tilt etc for certain cameras) in java code.
Improved handling of capture graph building for mpeg devices, which should now work more reliable
when no filter preferences have been set in the xml configuration and can also monitor during
capture. Most mpeg capture devices will not let you separately control preview and capture, though, 
so this is no real preview (StreamBuffer Engine support pending)
Fixed volume control for mp3 files.
Fixed bug that caused crashes on some multiplexed media that happened to have no audio.
dsj will by default now keep the aspect ratio of media fixed on resizing. Use lockAspectRatio(false) 
to enable free resizeing.


b_0_8, July 2006:
Lifted dsj's native part from the sketch phase to a properly structured layout. As a result
dsj can now handle multiple filtergraphs inside a single process space.
Support for writing Windows Media (via the export(..) method in DSMovie and all
file capture methods in DSCapture and DSDVCam). Requires encoders to be installed of course,
see Windows Media links below
dsj now comes with a java source filter that can inject java drawing commands into a DirectShow
filtergraph (see additional documentation in the "filters" directory).
Audio delivery to javasound now available in all high level classes (except DVCam) when format 
supports it.

b_0_7_53
framerate retrieval blocked audio only streams, fixed
removed false implementation of getPreferredSize that would cause swing resize commands to fail
on video display components

b_0_7_52
Finally returns correct frame rate values for movies and capture devices.
Supports playbackrates other than 1
Application may set dll path.

b_0_7_5: May 2006
Support for crossbar equipped (TV / analogue video) capture boards (Input Selection, TV tuning,
audio grabbing). 
Support for audio compression in DSMovie & DSCapture. Not in DSDVCam yet, as it takes some
non standard filters to feed DV audio into most the compressors.
Solved problems with programatically set format for capture devices and capture to file.
Improved handling of audio-only files & fixed bug with audio only streams.
Support for audio-only capture and export to non-avi files (wav, mp3). For the latter the WavDest 
DirectShow filter is required!! Google for WavDest.ax. 
Note that progress notification will not properly work when exporting to audio files;
DVCam & Capture classes may capture without preview, possibly improving realtime encoding results
on slower machines.
DVCam class returns tape time when in vcr mode.
DVD class supports title access.
New headless rendering mode in which dsj will not interfere with the filtergraph in any way after
construction except for basic controls (starting, stopping, etc)

Extended DSGraph class to read XGR (GraphEdit xml) files or raw xml passed as a String.
Also allows to manually insert and connect filters, involving the new DSFilter class. This is still
widely experimental and may change a bit in coming versions. Both scenarios still have big failure 
potential, especially when inserting wrapper filters (like "Avi Compressor", etc., DMOs should halfway work ok);
I plan to put up a download area for grf & xgr stuff. Check the website.

Some variants of static filter query methods in DSEnvironment. 

Some java api cleanup (hence the jump in version numbers). 
DSMovie is no longer the base class, but just one implementation of the new DSFiltergraph class.
All rendering and transport functionality that is shared by all the subclasses went from DSMovie 
into this class. Same for all the constant definitions. This will make some minor changes in 
application code necessary, but does not break anything in general. It hopefully makes the
api conceptually a little clearer and more extendable.

Most methods that directly or indirectly reference DirectShow Filters now 
take DSFilterInfo arguments instead of just name strings

JMF specific:
Special thanks to M.Göllnitz, who found out that providing JMF with at least a fake timestamp
in its DataSource read method instead of using Buffer.TIME_UNKNOWN will make things
running with halfway OK speed for RTP transmission. See updated JMF code in demo/JMF.



b_0_7_1:
Extends the capture class with static device query methods and more programmatic control 
over the filtergraph creation. Also better supports mpeg capture devices (video and audio) now.
DVCameras should also work properly with the capture class for video and audio (no device control
though,that's only available in the DSDVCam class).
Known issues & limitations: 
Capturing mpeg2 files from mpeg devices is not yet supported 
There is no proper support for crossbars yet (hardware donations welcome!)
Capturing to file tends to crash when you programmatically set the capture device's pin & format.
Open the default pin and use the native dialogs to change format instead.

dsj now uses some abstracting classes and an external XML setup file to handle the potentially 
more complex construction of capture graphs and to specify decoder preferences. See DSFilterInfo &
DSEnvironment in the javadocs.

Extended encoding support(DSMovie, DSCapture & DSDVCam). dsj works seamlessly with standard Windows 
encoders and most available third party encoder filters like Xvid, 3ivX etc. Especially in terms of
DivX encoders it is worth experimenting with what encoder to use for what class / source format.
They differ quite a bit in what datarates they manage to encode in "realtime"

Improved support for and some documentation about receiving media streams from VLC, Windows Media
Encoder etc.

Two new event notification flags. Will be raised on loop and when a movie has finished playing.

Changed some of the DVD constructors and removed others. Check your code, there is no more path
parameter in any of them.
Corrected fullscreen behaviour of 16:9 DVDs. Note that DVDs that change format in between titles
or chapters will still cause problems.

Added two new JMF DataSources so that JMF Manager can now construct a processor with dsj as source.
Alongside this there is a new setup flag: JAVA_POLL_RGB that will cause native reformatting of dsj's
original BGR data to RGB. This is intended for JMF RTP Transmission, as the standard JMF processor
would neither complain nor do anything usefull with the BGR data...(it still is slow, though and I'm
not sure what is causing this, The DataSources are called / push at reasonable rates...Everybody who 
wants to fix this is welcome, I am just not into JMF at all)

As usual you can extend the available functionality by installing additional DirectShow filters.
See resources on the end of this document.


b_0_7.01:
provides a bugfix for JAVA_POLL mode and a quick hack, that will avoid potential decoder problems with 
mpeg capture devices by giving preference to the mostly non-mpeg preview output. 

b_0_7:
dsj now uses DirectDraw technics as its default native rendering engine. This fixes issues with 
scaling heavyweight material. As a sideeffect it also allows to correct the aspect ratio of DVD 
playback on rendering, so that 16:9 and 4:3 material is now correctly displayed (DirectDraw renderer 
only!) In case of problems with this you may force the previous GDI based engine to be used by setting 
the NATIVE_FORCE_GDI flag on construction. 
Also there have been some improvements to the way data is pushed over to java, so that the achievable 
framerate should be reasonably higher with heavier sources.

A new DSMovie subclass - DSCapture - provides access to non firewire/DV DirectShow video and audio 
capture devices. This still has problems with multioutput TV cards and especially with ones that 
deliver mpeg streams. It will also undergo some changes in the coming versions, introducing more 
programatical control. 

DSMovie can now deliver audio to java. This does not yet work for all formats, in particular it does 
not work for WindowsMedia...

Encoding functionality has been added to the DSMovie class and DSCapture as well. Careful though: 
only the standard set of encoders may work properly. This is very beta...

Fixes: 
Problems arising from wrong duration values should be resolved now. Thanx for the hints, I never watch 
long movies...;-).Interestingly, most of the DirectX SDK has the same problems here.

DivX now works with java side rendering
This version is potentially a little buggy and should not be redistributed. Contact me for a 
redistributable copy if you need one.


b_0_6_6:
Another new DSMovie subclass: DSDVCam wraps DirectShow api for IEEE1394 DV-Camcorders. This offers 
full hardware device control and supports both camera and VCR mode. Tape to file transfer is supported
. File to tape is untested, as I do not have a camera capeable of recording via firewire at hand.
Introduced an additional constructor for the DSDvd class, that allows to specify mpg2 and ac3 
decoders. This makes sure that compatible filters are used and also significantly speeds up the 
construction of the filtergraph, see below.

b_0_6_5:
Integrated two DSMovie subclasses: DSDvd & DSGraph. First offers some extra functionality for DVD 
playback, latter handles DirectShow filtergraph construction from GraphEdit's .grf files. 
Both classes can either be constructed directly or through the new factory method in DSMovie. 
See javadocs.

DVD issues: 
Decoder filter lookup: Finding the right components can be a little tricky. This is especially true 
for building DVD graphs as there are tons of mpg2 & a3c decoders around, and usually people will have 
multiple decoders installed (most likely without knowing about it) that may or may not work with each 
other. Not all DSDvd constructors allow to specify preferences here (as native DVDplaying software, 
that would also probably bring it's own decoders, would). So it relies on trial and error when 
choosing decoders, I decided to leave some debugging printouts in here, so you can see what video & 
audiodecoders will be picked and which may be causing problems in case there are problems. I have 
made good experiences with the Elecard decoder pack (www.elecard.com, evaluation copies for developers
are available) and for AC3 Audio there's a very good free GPL liscensed project at 
sourceforge.net/projects/ac3filter. Others may work equally well. Others may not at all and crash 
right away. A good resource for DirectShow Filters is www.free-codecs.com. A nice utility for managing
filters is the Softella Filter Manager (www.softella.com).

Menu navigation: 
In order to make mouse operation of DVD menus work from java windows I had to implement an ugly hack 
that both does not look good & has a little performance penalty. Mouse menu operation must therefore 
be explicitly enabled by setting flags. Relative navigation in oposition will allways work (The demo 
implements it on the movie controller, but you can roll your own of course). Also note that DVD setup 
may take a significant amount of time (especially when the drive has not been active before). Besides 
that there are still some issues with 16:9 formats and moviemode.
DSGraph is a mere utility class. It will reproduce filtergraphs put together in Microsoft's GraphEdit
tool (comes with the DirectX SDK but is also available alone). Theoretically it should enable dsj to 
bridge almost everything that DirectShow can render to 2D surfaces over to java. Supplied graph's 
MUST be terminated by any of the DirectShow Video Renderers. dsj will remove that from the graph and 
hook its own rendering sink in instead. Two examples are supplied. One simply refers to a File URL 
(and should work for everybody),the second uses a very standard USB webcam as source. It will only work
when you have a webcam connected and it happens to be compliant with mine...;-) If it does not, but 
this sounds interesting to you, start considering to visit Microsoft's website.
Finally: All Quicktime dependencies have been removed from the MovieController class. Apologies again, though I still think, that every PC should ship with Quicktime for Java.

b_0_6:
Important: The DSMovie constructors have changed. The constructor that takes only a filename
argument now creates a native rendering environment. Last version's demo code will fail with
this release!!
dsj now supports native rendering into java frames and java fullscreen windows, as well as delivering 
DirectShow image data in a BufferedImage. You may also set it up in a way that uses the latter approach,
but takes care of all rendering internally and offers a lightweight autoredrawing panel to the application.
Additional "enhancements":
- Framestepping support (still problematic with some VBR encodings)
- Movies will correctly resize with the enclosing window
- native mode plays DivX movies (given the codec is installed of course), does not work in java mode yet.
- MovieController now keyboard operateable
Additional "issues":
- Heavyweight movies (like DV or mpg2) may cause extreme cpu loads and audio dropouts when stretched
to full screen. probably need to get rid of GDI here.  

Version b_0_5:
This version supports polling mode only (ie. image buffers are passed from java to c++, get filled 
and returned). You should not expect performance wonders of it. It will play reasonable size videos
with reasonable framerates, but may start gargling on higher resolutions. You may adjust the frequency 
in which buffers are pulled to dampen that. The next dsj version will do native fullscreen rendering
and native rendering into java windows.

___________________________________________________________________________________________________

links, resources:

decoders/encoders:
http://ffdshow.info
http://www.elecard.com
http://www.sourceforge.net/projects/ac3filter
http://www.illiminable.com/ogg/
http://www.theora.org/
http://sourceforge.net/projects/guliverkli
http://esby.free.fr/CelticDruid/mirror/Media%20Player%20Classic/external%20filters/unicode/FLVSplitter.7z
http://www.3ivx.com/download/index.html
http://www.radlight.org/
http://www.standardmpeg.com/
http://www.hdx4.com/
http://www.leadcodecs.com/Download/eval.htm

Windows Media Encoder
http://www.microsoft.com/downloads/details.aspx?displaylang=en&FamilyID=5691ba02-e496-465a-bba9-b2f1182cdf24
http://www.microsoft.com/windows/windowsmedia/licensing/Licensing_Win_Apps_Encoder.aspx


ColorSpaceConverter
http://www.elecard.com/products/products-pc/sdk/encoder-sdk/
http://www.gdcl.co.uk/YUVxfm.zip

DirectShow utilities
http://www.softella.com  - filter management tool
http://www.radlight.org/products/filter_manager/index.php
http://www.headbands.com/gspot/ - codec information appliance

 
DVB / ATSC / MPEG TS
http://forums.dvbowners.com/
http://dvbportal.de/

capture drivers etc:
http://www.umediaserver.net - capture source for AXIS IP Cameras
http://www.hmelyoff.com/index.php?section=4 - screen grabbing capture source
http://btwincap.sourceforge.net/ - alternative, feature-rich driver for standard TV capture boards
http://www.splitcamera.com/ - free utility to share capture devices among several applications
http://www.medialooks.com/ 

misc:
http://www.gdcl.co.uk/ website of the original author of the DirectShow architecture
http://www.doom9.org/ everything about digital video and its steady evolution.
http://www.fourcc.org/fcccodec.htm all you ever wanted to know about four char codes
http://www.m4if.org mpeg industry forum
http://tsviatko.jongov.com/index_projects.htm - stream data visualizers and DSFilters

http://www.radiosites.de/internetradio_faq.shtml#server some links to cheap or free WM Servers

____________________________________________________________________________________________________

DirectShow, DirectX, Windows, WindowsMedia, .wmv & .asf are registered trademarks of Microsoft Corporation
Java is a registered trademark of Sun Microsystems Inc.
Other mentioned product names and abrevations belong to their respective trademark owners / copyright holders.
____________________________________________________________________________________________________

The default implementation of the SwingMovieController class uses GUI graphics from Sun's free GUI kit,
redistributed under the following license:


Copyright 2000 by Sun Microsystems, Inc. All Rights Reserved.

Sun grants you ("Licensee") a non-exclusive, royalty free, license to use, and redistribute this 
software graphics artwork, as individual graphics or as a collection, as part of software code or 
programs that you develop, provided that i) this copyright notice and license accompany the software
graphics artwork; and ii) you do not utilize the software graphics artwork in a manner which is 
disparaging to Sun. Unless enforcement is prohibited by applicable law, you may not modify the 
graphics, and must use them true to color and unmodified in every way.

This software graphics artwork is provided "AS IS," without a warranty of any kind. ALL EXPRESS OR 
IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN AND ITS LICENSORS 
SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR 
DISTRIBUTING THE SOFTWARE GRAPHICS ARTWORK.

IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT,
INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF 
THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE SOFTWARE GRAPHICS ARTWORK, 
EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.

If any of the above provisions are held to be in violation of applicable law, void, or unenforceable 
in any jurisdiction, then such provisions are waived to the extent necessary for this Disclaimer to 
be otherwise enforceable in such jurisdiction.
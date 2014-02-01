# Poor Man's CMS (pmcms)

... a very basic CMS generating static html pages.

pmcms is distributed under the [LGPL](http://www.gnu.org/licenses/lgpl-3.0.html) and [MPL](http://www.mozilla.org/MPL/MPL-1.1.html) Open Source licenses. This **dual copyleft licensing model** is flexible and allows you to choose the license that is best suited for your needs. The Open Source licenses are intended for:

* Integrating pmcms into Open Source software.
* Personal and educational use of pmcms.
* Integrating pmcms in commercial software while at the same time satisfying the terms of the Open Source license.

**Hint:** pmcmns is a further development of the [good old Poor Man's CMS](http://poormans.sourceforge.net/).

## Changes 

* 3.0.0-SNAPSHOT 
  * changed to commons-codec for encoding/decoding passwords
  * updated to ckeditor 4.*
  * updated to swt 4.2.1
  * updated basic eclipse libs
  * new swt jar loading behavior
  * updated to spring 3.2.1
  * new property handling. there is now a strict difference between common, user and site properties
  * updated CodeMirror to 3.*
  * new handling of images of a gallery, they are now separated from the other file resources
  * merged the ImageLinkTool with the GalleryLinkTool
  * rewritten the handling of resource files
  * simplified the ImageTagTool
  * updated to jetty 9.*
  * updated to slf4j 1.7.2
  * added C5Connector.Java 0.3
  * added CKEditor.Java 1.0
  * updated to ant 1.9.2
  * change the loading behavior of the swt-jars (64bit jars are respected)
  * build the general conditions to get a 'portable' version

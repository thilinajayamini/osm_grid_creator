========================
BUILD OUTPUT DESCRIPTION
========================

required input 
--------------------------------------------------------------
* bounding box of the area you planing to edit
 - go to https://www.openstreetmap.org/export 
 - select the area you going to edit (lager are which need to create grid)
 - you can see on left side top, bottom, left and right values. get those values and update in settings.cfg file
* if you planing to work with multiple areas in same time.chose unique id and set it in as uid in settings.
 


How to run
--------------------------------------------------------------
java -jar "GridCreator.jar" 


Out put
--------------------------------------------------------------
* this will create Grid.htm file. 
* it will contain grid with image off open street map (area you choose to update).
* inside each cell in grid there will be link ( marked as J ).
* once you click this link JOSM will load respective cell area in to editor.


Usage
--------------------------------------------------------------
* once web page created rename it with proper name.
* store it in proper directory/folder with css file and script file.
* change this file path or file name later will cause corruption in stored data.
* click on the link (J) then JOSM will load the respective area.
* once done right click on link then pop up will come.
* confirm the pop up then edited cell will mark with green.


Notes:
--------------------------------------------------------------
* you need to use JOSM (https://josm.openstreetmap.de) with remote controller enabled.
	- [In the JOSM menu select Edit/Preferences.../ or press F12 .Click on the remote control icon .
	   Select "Enable Remote Control" and click OK.]

* this will not run behind proxy . need direct internet connection. 
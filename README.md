# ImageJ Processing

Saving Danny time doing her manual work (...see: https://xkcd.com/1205/)

## Usage

### Part 1
- Populate the `resources/density` folder with CSV pairs, following the pattern `NAME-ROI.csv` and `NAME-particles.csv`. `NAME` can be anything you like, as long as they match up together.
- Call the main method with the parameter `1` to run these files through the Density processing.
- Check the processed output in `output/density/output.csv`. 

### Part 2
- If the output of Part 1 is as expected, rename the output file with the format `ISH {DESC} {FILETYPE}.csv` and move it to the `resources/sections` folder. `{DESC}` can be whatever you like. `{FILETYPE}` must be one of the following (case-sensitive):
    - `PVN` - Requires **4** rows per chunk, and will output the following row captions:
      - Magno R, Parvo R, Magno L, Parvo L
    - `PFC` - Requires **6** rows per chunk, and will output the following row captions:
      - IL R, PrL R, Cg1 R, IL L, PrL L, Cg1 L
    - `Hippocampus` - Requires **8** rows per chunk, and will output the following row captions:
      - CA1 L, CA2 L, CA3 L, DG L, CA1 R, CA2 R, CA3 R, DG R
    - `Amygdala` - Requires **10** rows per chunk, and will output the following row captions:
      - BLA R, CeA R, CoA R, BMA R, MeA R, BLA L, CeA L, CoA L, BMA L, MeA L
    - You can add as many Part 1 outputs in this folder as you like and process them all at once.

- Call the main method with the parameter `2` to run these files through the Section Averages
processing.
- Check the processed output(s) in `output/sections/ISH {DESC} {FILETYPE}_sections.csv`. 

### Part 3
- If the output of Part 2 is as expected, move the files to the `resources/overall` folder (do not
rename these files).
- Call the main method with the parameter `3` to run these files through the Overall Averages 
processing.
- Check the processed output(s) in `output/sections/ISH {DESC} {FILETYPE}_sections_overall.csv`. 

## Processing Details

### Part 1 - Density Calculations

The first part of the processing will read in resources CSVs in their pairs, extract the 2nd column of the `-particles` file, which should be a list of `COUNT` values.  
It then also extract every 8th cell in the 2nd row of the `-ROI` file, which should correspond to `AREA` values.  
If there is the right number of each, it will divide the particles by the area, to give a density.  
All three pieces of information are then written to a CSV, in `output/output.csv` for each pair of particle/area values.

#### Sample inputs

_/resources/density/{NAME}-particles.csv_
```
Slice,Count,Total Area,Average Size,%Area,Mean,Mode,IntDen
BP1 s1 with scale.tif,9132,7633.044,0.836,3.541,255,255,213.143
BP1 s1 with scale.tif,7349,5777.305,0.786,3.711,255,255,200.464
BP1 s1 with scale.tif,6481,5615.908,0.867,3.203,255,255,220.962
BP1 s1 with scale.tif,7495,6622.049,0.884,3.689,255,255,225.300
```

_/resources/density/{NAME}-ROI.csv_
```
,Area1,Mean1,Mode1,Min1,Max1,IntDen1,%Area1,RawIntDen1,Area2,Mean2,Mode2,Min2,Max2,IntDen2,%Area2,RawIntDen2,Area3,Mean3,Mode3,Min3,Max3,IntDen3,%Area3,RawIntDen3,Area4,Mean4,Mode4,Min4,Max4,IntDen4,%Area4,RawIntDen4
1,215550.125,112.392,0,0,255,24226206,44.075,474174528,155693.562,97.948,0,0,255,15249833,38.411,298481824,175321.219,151.079,255,0,255,26487344,59.247,518431328,179488.125,138.267,255,0,255,24817352,54.222,485744896
```

#### Sample output

_/output/density/output.csv_
```
BP1 s1 with scale
,Area,Particles,Density
,215550.125,9132,0.04236601579331026
,155693.562,7349,0.04720169482666213
,175321.219,6481,0.0369664324544766
,179488.125,7495,0.04175763716958991

BP1 s2
,Area,Particles,Density
,117745.992,3658,0.031066874870781164
,202428.953,6432,0.031774110890155124
,119321.344,3757,0.031486403639570136
,165411.438,6782,0.04100079221849217

BP1 s3
,Area,Particles,Density
,97975.797,2982,0.03043608821064247
,159230.828,4047,0.02541593264841906
,69451.203,1031,0.014844955241452046
,148344.688,4460,0.030065114296509224
```

### Part 2 - Section Averages

The second part of the processing reads in files in the format outputted by Part 1.
The calculated densities from each section are grouped into `s1`, `s2`, `s3`, and `s4`, and an overall average is calculated for sections 1-4.
Note: if the input is missing any full sections, these will be populated with zeroes, and disregarded from the averages.  
Also note that the 'chunking' of the sections is performed naively; that is, if the number of rows in the input is misaligned, an exception will be thrown and processing will not continue.  
Empty rows (or rows of just commas) will be trimmed out prior to chunking.

#### Sample input

_/resources/sections/ISH A PVN.csv_
```
BP1 s1 with scale
,Area,Particles,Density
,215550.125,9132,0.04236601579331026
,155693.562,7349,0.04720169482666213
,175321.219,6481,0.0369664324544766
,179488.125,7495,0.04175763716958991

BP1 s2
,Area,Particles,Density
,117745.992,3658,0.031066874870781164
,202428.953,6432,0.031774110890155124
,119321.344,3757,0.031486403639570136
,165411.438,6782,0.04100079221849217

BP1 s3
,Area,Particles,Density
,97975.797,2982,0.03043608821064247
,159230.828,4047,0.02541593264841906
,69451.203,1031,0.014844955241452046
,148344.688,4460,0.030065114296509224
```

#### Sample output

Note that since there was no 'BP1 s4' in the input, these have been replaced with `0.0`. The row labels (and chunk sizes) are determined by the file type in the file name.
_/output/sections/ISH A PVN_sections.csv_
```
,BP1 s1,BP1 s2,BP1 s3,BP1 s4,all sections mean
Magno R,0.04236601579331026,0.031066874870781164,0.03043608821064247,0.0,0.034622992958244635
Parvo R,0.04720169482666213,0.031774110890155124,0.02541593264841906,0.0,0.03479724612174544
Magno L,0.0369664324544766,0.031486403639570136,0.014844955241452046,0.0,0.02776593044516626
Parvo L,0.04175763716958991,0.04100079221849217,0.030065114296509224,0.0,0.03760784789486377
```

### Part 3 - Overall Averages

The final part of the processing reads in files in the format outputted by Part 2.
The 'BP' sections are grouped to align with the actual rat group that they correspond with.
Next, the calculated left + right average densities from each segment are averaged together, and then an overall average is calculated from these paired averages.
Finally, the overall sum, average, standard deviation, and standard error of the mean is calculated for each paired average, and the overall average, on a rat-group level.

#### Sample input

_/resources/overall/ISH A PVN_sections.csv_
```
,BP1 s1,BP1 s2,BP1 s3,BP1 s4,all sections mean
Magno R,0.04236601579331026,0.031066874870781164,0.03043608821064247,0.0,0.034622992958244635
Parvo R,0.04720169482666213,0.031774110890155124,0.02541593264841906,0.0,0.03479724612174544
Magno L,0.0369664324544766,0.031486403639570136,0.014844955241452046,0.0,0.02776593044516626
Parvo L,0.04175763716958991,0.04100079221849217,0.030065114296509224,0.0,0.03760784789486377
```

#### Sample output

_/output/overall/ISH A PVN_sections_overall.csv_
```
Male Control Stress,Magno R+L mean,Parvo R+L mean,Total mean
BP1,0.031194461701705447,0.03620254700830461,0.03369850435500503
N,0.031194461701705447, 0.03620254700830461, 0.03369850435500503
Average,0.031194461701705447, 0.03620254700830461, 0.03369850435500503
SD,0.0, 0.0, 0.0
SEM,0.0, 0.0, 0.0
```

## Notes

Still very much a hacky make-it-work first draft. May revisit if further analysis is required.
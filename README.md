# ImageJ Processing

Saving Danny time doing her manual work

## Usage

Stick a bunch of CSVs into one-level-deep folders int the `resources` root folder. They must be in 
pairs, following the pattern `NAME-ROI.csv` and `NAME-particles.csv`. `NAME` can be anything you 
like, as long as they match up together.

The program will read in these CSVs in their pairs, extract the 2nd column of the `-particles` 
file, which should be a list of `COUNT` values. It will also extract every 8th cell in the 2nd row
of the `-ROI` file, which should correspond to `AREA` values. If there is the right number of each,
it will divide the particles by the area, to give a density. All three pieces of information is then
written to a CSV, in `output/output.csv` for each pair of particle/area values.

## Notes

Very much a hacky make-it-work first draft. May revisit if further analysis is required.
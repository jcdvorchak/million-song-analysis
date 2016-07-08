# million-song-analysis
Explore the Million Song Dataset

http://labrosa.ee.columbia.edu/millionsong/

**What does repetition (chorus/verse/etc.) within a track look like by genre, location and decade?**

Compare the predefined sections of a track to each other based on the data within their segments (loudness,pitch,timbre) and their length. Flatten out the segment data for each section and check if the the cosine similarity between the segment data of two sections is within a certain threshold and if the sections are of a similar length.

Processed 500k of the Millon Song Dataset using...
- src/main/scala/msong/LocalRuns.trackAnalysisPsv for a pipe separated format of section matches
- src/main/scala/msong/LocalRuns.sectionSimilarityCounts for aggregation on genre,decade,location from the output of msong.LocalRuns.trackAnalysisPsv

**Documents**

Some photos of a few quick Tableau visualizations are located in the 'docs' directory! They are based on the output of 500,000 tracks processed through msong.LocalRuns.trackAnalysisPsv + msong.LocalRuns.sectionSimilarityCounts.

docs/sampleoutput contains the output of LocalRuns.trackAnalysisPsv and LocalRuns.sectionSimilarityCounts for 10,000 files.

# TreadPace

## Introduction

TreadPace is a GPS running application for Android that tracks the beginning of a user's run and uses that as their baseline pace for the duration of the run.

## Installation

TreadPace is (soon to be) available on the Google Play store. To install & run TreadPace yourself, clone the repository and create an XML string resource named GoogleMapsAPIKey that has the Google Maps SDK for Android enabled.

## Overview

TreadPace seeks to bridge the gap between running on the treadmill versus running outside. On a treadmill, a user is able is set their pace and know that they're consistently running at the same speed for their run. However, when you run outside, you can be bogged down by wind or hills and lose track of the pace you set out to run initially. 

TreadPace bridges this gap by taking the first three splits (roughly thirty seconds) of a person's run and uses that as their baseline pace. The user will know how many thirty second splits they ran and whether they were consistent with their pace throughout the run.


## Screenshots

<img src="images/home_screen.png" alt="Home screen" width="45%" height="45%" /> <img src="images/run_before.png" alt="Before run"  width="45%" height="45%" /> 
<img src="images/run_inprogress.png" alt="Run in progress" width="45%" height="45%" /> <img src="images/review.png" alt="Review run"  width="45%" height="45%" /> 

## Potential improvements to make
- Handle permissions more gracefully.
- Improve UI/UX on run page (make it more clear what numbers represent).
- Improve tracking capabilities (throw out outlier points, ability to remove certain splits).
- Allow more options for pace checking (% threshold, flat amounts).

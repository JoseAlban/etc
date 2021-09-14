# 3. Regional distribution of origins

Date: 2021-09-14

## Status

Accepted

## Context

We need to decide how many origins we will provision, and where.

Our users are distributed, as of 2021-09-14, as follows:
- UK - 66%
- Australia - 14%
- Japan - 10%
- Others - 10%

That data helps us decide where to put origins, but not how many of them.
For the latter, we could get an average (and cut off spikes) of data uploaded, per region, per month.

Formula: cut out values above max of 95th percentile, then average it.

- UK - 20GB/mo
- Australia - 500GB/mo
- Japan - 10GB/mo
- Others - 5GB/mo

## Decision

- Origin regions and quantity
    - UK - 1
    - Australia - 5
    - Japan - 1
    - Default: redirect to Australia


## Consequences

- We expect users to have the best latency possible, no matter which region they are at
- We will be measuring latency and region, and monthly review to decide whether to change this configuration
- We can still scale in or out those quantities depending on our monthly measurements

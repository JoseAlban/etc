# 2. CDN choice

Date: 2021-09-14

## Status

Accepted

## Context

(Disclaimer: fake context, for the assignment purpose)

Out of many cloud providers, one CDN solution should be chosen

- AWS
    - Cheap, fast and feature rich
- GCP
    - It would be more complicated to integrate with our current systems
    - Out-of-the-box metrics are great: it measures requests, latency and cache hits for free
- Azure
    - The origin refresh hit was causing a latency spike which was deemed unacceptable

## Decision

AWS Cloudfront was chosen, as the least friction to keep us making progress

## Consequences

Pros:
- AWS makes it easier to integrate with our current AWS services

Cons:
- AWS does not provide the metrics that we need for our SLAs
    - We will need to build that out of the AWS API and plug into our dashboard
- Worst case scenario is that AWS discontinues Cloudfront
    - In this case, we would have to re-evaluate options at the time

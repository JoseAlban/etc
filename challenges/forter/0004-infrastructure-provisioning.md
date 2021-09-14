# 4. Infrastructure provisioning

Date: 2021-09-15

## Status

Accepted

## Context

To deploy and operate this system, we need to pick the right tool for provisioning, and avoid as much as possible any manual step.

Manual steps will be specially harder to track on this deployment, as it involves similar instances but in different regions.

We already have Terraform provisioned resources in our cloud provider, so it's easier to carry on with the same tool.

## Decision

- Provision all resources via terraform
- Don't do any manual actions on our cloud provider UI

## Consequences

- Terraform upgrades could break deployment
- Modularising this component becomes easier with terraform
- We could revert decision in future in case a new provisioning tool comes to market and we need migrating all provisioned resources

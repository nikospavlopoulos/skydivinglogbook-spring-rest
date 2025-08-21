#!/usr/bin/env bash
# Bulk create milestones and epic issues in GitHub repo using gh CLI
# Repo: nikospavlopoulos/skydivinglogbook-spring-rest

REPO="nikospavlopoulos/skydivinglogbook-spring-rest"

# Array of milestone titles
declare -a MILESTONES=(
  "Project Setup and Initial Configuration"
  "Domain Model (Entities) – Repositories – DTO"
  "Exceptions – Mapper – Service"
  "Authentication – Security"
  "REST Controllers | API Endpoints"
  "Swagger/OpenAPI – API Documentation"
  "Frontend UI – Basic Client for the REST API"
  "Docker – Containerization and Deployment Setup"
)

# Loop through milestones
for i in "${!MILESTONES[@]}"; do
  TITLE="${MILESTONES[$i]}"
  MD_FILE="./issues/milestone${i}.md"

  echo "Creating milestone: Milestone $i – $TITLE"

  # Create milestone and get its number
  MILESTONE_ID=$(gh api -X POST repos/$REPO/milestones -f title="Milestone $i – $TITLE" | jq -r '.number')

  if [ -z "$MILESTONE_ID" ] || [ "$MILESTONE_ID" == "null" ]; then
    echo "Error creating milestone $TITLE. Skipping issue creation."
    continue
  fi

  echo "Milestone created with ID: $MILESTONE_ID"
  echo "Creating issue for milestone: Milestone $i – $TITLE"

  # Create issue linked to milestone
  gh issue create --repo $REPO \
    --title "M${i} – $TITLE" \
    --milestone $MILESTONE_ID \
    --body-file "$MD_FILE"

  echo "Issue created for Milestone $i – $TITLE"
done

echo "All milestones and issues processed."


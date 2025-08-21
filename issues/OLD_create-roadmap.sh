bash
#!/usr/bin/env bash
# Bulk create milestones and epic issues in GitHub repo using gh CLI
# Repo: nikospavlopoulos/skydivinglogbook-spring-rest

REPO="nikospavlopoulos/skydivinglogbook-spring-rest"

# -------- Milestone Creation --------
gh api -X POST repos/$REPO/milestones -f title="Milestone 0 – Project Setup and Initial Configuration"
gh api -X POST repos/$REPO/milestones -f title="Milestone 1 – Domain Model (Entities) – Repositories – DTO"
gh api -X POST repos/$REPO/milestones -f title="Milestone 2 – Exceptions – Mapper – Service"
gh api -X POST repos/$REPO/milestones -f title="Milestone 3 – Authentication – Security"
gh api -X POST repos/$REPO/milestones -f title="Milestone 4 – REST Controllers | API Endpoints"
gh api -X POST repos/$REPO/milestones -f title="Milestone 5 – Swagger/OpenAPI – API Documentation"
gh api -X POST repos/$REPO/milestones -f title="Milestone 6 – Frontend UI – Basic Client for the REST API"
gh api -X POST repos/$REPO/milestones -f title="Milestone 7 – Docker – Containerization and Deployment Setup"

# -------- Issues Creation --------
# Each issue corresponds to one milestone and contains your full roadmap text

gh issue create --repo $REPO --title "M0 – Project Setup and Initial Configuration" \
  --milestone "Milestone 0 – Project Setup and Initial Configuration" \
  --body-file ./issues/milestone0.md

gh issue create --repo $REPO --title "M1 – Domain Model (Entities) – Repositories – DTO" \
  --milestone "Milestone 1 – Domain Model (Entities) – Repositories – DTO" \
  --body-file ./issues/milestone1.md

gh issue create --repo $REPO --title "M2 – Exceptions – Mapper – Service" \
  --milestone "Milestone 2 – Exceptions – Mapper – Service" \
  --body-file ./issues/milestone2.md
	
gh issue create --repo $REPO --title "M3 – Authentication – Security" \
  --milestone "Milestone 3 – Authentication – Security" \
  --body-file ./issues/milestone3.md
	
gh issue create --repo $REPO --title "M4 – REST Controllers | API Endpoints" \
  --milestone "Milestone 4 – REST Controllers | API Endpoints" \
  --body-file ./issues/milestone4.md
	
gh issue create --repo $REPO --title "M5 – Swagger/OpenAPI – API Documentation" \
  --milestone "Milestone 5 – Swagger/OpenAPI – API Documentation" \
  --body-file ./issues/milestone5.md
	
gh issue create --repo $REPO --title "M6 – Frontend UI – Basic Client for the REST API" \
  --milestone "Milestone 6 – Frontend UI – Basic Client for the REST API" \
  --body-file ./issues/milestone6.md
	
gh issue create --repo $REPO --title "M7 – Docker – Containerization and Deployment Setup" \
  --milestone "Milestone 7 – Docker – Containerization and Deployment Setup" \
  --body-file ./issues/milestone7.md

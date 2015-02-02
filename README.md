# Activate_benchmark
Simple insert and select benchmark using Activate Framework (http://activate-framework.org/) and either in-memory or Postgresql database.

Data model:
Author - (1-to-Many) - Book - (Many-to-Many, via AwardPresentation) - Award

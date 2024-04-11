# spotify-wrapped

## Setup
___
### Environment Variables
1. Create env file at ```app/src/main/assets/env``` or rename ```app/src/main/assets/env.example``` to env
2. Fill in variables
    ```
   CLIENT_ID=
   CLIENT_SECRET=
   REDIRECT_URI=
   ```
3. Make sure to also modify build.gradle with your own redirect uri
    ```
    manifestPlaceholders = [redirectSchemeName: "spotify-wrapped", redirectHostName: "auth"]
    ```
## User Stories
___
### Base User Stories
- [x] User Story #1
- [ ] User Story #2
### Optional
- [ ] Friend View (3/+2)
- [ ] Holiday Wrapped (5/+2)
- [ ] Quiz / Games (5/8/13)
- [ ] Music Recommendations (3/+2)
- [x] Timespan (5)
- [ ] Public Spotify Wrapped Page (8/+3/+13/+2/+3)
- [x] CI/CD Pipeline (5/+1) 
- [x] Firebase (8)
- [x] Templates for PRs and issues (1)

Current Score: ```20/32```
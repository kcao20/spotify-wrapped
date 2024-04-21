# spotify-wrapped
[Website](https://88cs2340.wixsite.com/spotify-wrapped-armi)
## Setup
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
### Base User Stories
- [x] User Story #1
- [x] User Story #2
### Optional
- [x] Audio Clips [#5] (13pts)
- [x] Timespan [#8] (5pts)
- [x] CI/CD Pipeline [#10] (5/+1pts)
- [x] Firebase [#12] (8pts)
- [x] Templates [#13] for PRs and issues (1pts)

Current Score: ```33/28```
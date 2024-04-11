# spotify-wrapped

## Setup
___
### Environment Variables
Create env file at ```app/src/main/assets/env```
```
CLIENT_ID=
CLIENT_SECRET=
REDIRECT_URI=
```
Make sure to also modify build.gradle with your own redirect uri
```
manifestPlaceholders = [redirectSchemeName: "spotify-wrapped", redirectHostName: "auth"]
```
## User Stories
___
### Base User Stories
- [ ] User Story #1
- [ ] User Story #2
### Optional
- [ ] Friend View (3/+2)
- [ ] Holiday Wrapped (5/+2)
- [ ] Quiz / Games (5/8/13)
- [ ] Music Recommendations (3/+2)
- [ ] Timespan (5)
- [ ] Public Spotify Wrapped Page (8/+3/+13/+2/+3)
- [x] CI/CD Pipeline (5/+1) 
- [x] Firebase (8)

Current Score: ```14/32```
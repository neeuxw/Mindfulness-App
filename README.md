README.txt
----------

1. PREPARATION
   • Install Android Studio (latest version recommended): https://developer.android.com/studio
   • Ensure Git is installed and configured:
     - On macOS/Linux: `git` via package manager
     - On Windows: Git bundled with Android Studio, or install from https://git-scm.com
     - Optionally verify in Android Studio: Settings → Version Control → Git → Test connection

2. CLONE FROM GITHUB
   Option A: Using Android Studio UI
     - Open Android Studio
     - Go to File → New → Project from Version Control → Git
     - Paste the repository URL (e.g., https://github.com/username/project.git)
     - Choose a local directory, then click **Clone** :contentReference[oaicite:1]{index=1}

   Option B: Using Terminal
     ```bash
     cd /path/to/your/projects
     git clone https://github.com/username/project.git
     ```
     Then in Android Studio: File → New → Import Project → select cloned folder :contentReference[oaicite:2]{index=2}

3. OPEN AND SYNC IN ANDROID STUDIO
   • Once cloned/imported, Android Studio will auto-detect it as a Gradle project.
   • If not, go to: File → Sync Project with Gradle Files.
   • If file structure appears empty, click the **Refresh** icon in the Gradle panel :contentReference[oaicite:3]{index=3}.

4. RUN THE APP
   • Connect an Android device or start an emulator.
   • Select the app module (usually named `app`), then click ▶ **Run** or **Debug**.

5. TIPS & TROUBLESHOOTING
   • **Missing dependencies or errors?** Try:  
     - File → Sync Project with Gradle Files  
     - Build → Clean Project → Rebuild Project
   • **Gradle sync issues?** Ensure `build.gradle` files are valid and compatible with your Android Studio version.
   • **UI too minimal?** Use Project view (left pane) to see all files clearly under: `app/src/main/java/…`, manifests, Gradle Scripts, etc.

6. READY TO CODE!
   You’ve now successfully cloned and loaded the project into Android Studio. From here:
   - Explore or modify source code under `app/src/main/java`
   - Add features, fix bugs, or build APK via Build → Build Bundle(s) / APK(s)

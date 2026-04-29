# Personal Health Assistant (On-device + Telehealth)

Short description
- Mobile assistant that aggregates local sensor data (steps, heart rate), runs lightweight on-device ML models for anomaly detection, and enables secure remote consultations (telehealth) via WebRTC.

Why this is useful & unique
- Combines on-device privacy-preserving ML with open standards for teleconsultation. Focus on actionable alerts, data portability, and integration with Health Connect.

Core features
- Health Connect + Google Fit integration for steps/metrics
- On-device TFLite model for anomaly detection (heart-rate/spike detection)
- Secure chat and WebRTC video call (Jitsi or Janus) for teleconsultation
- Exportable health summaries (PDF/CSV) and share with clinicians

Recommended tech & free tools
- Android: Kotlin, Jetpack Compose, Health Connect API
- On-device ML: TensorFlow Lite, ML Kit for prebuilt models
- Telehealth: Jitsi Meet SDK (open-source) or WebRTC via PeerConnection
- Backend (optional): Supabase or Firebase for auth and data sync
- Notifications & scheduling: WorkManager, AlarmManager

Initial scaffold tasks
1. Create Android Studio project skeleton (Kotlin, Compose).  
2. Add Health Connect integration sample (read steps/metrics).  
3. Add TFLite model loader stub and sample inference path.  
4. Integrate Jitsi SDK quickstart for calls.

Next steps I can do now
- Create starter project files and add Health Connect sample code.  
- Add TFLite model loader utility and sample test harness.

import React, { useEffect } from 'react'
import SpaceScroll from './SpaceScroll'
import './Home.css'
import {
  FaCode,
  FaPaintBrush,
  FaCogs,
  FaTools,
  FaComments,
  FaUsers,
  FaPuzzlePiece,
  FaSyncAlt,
} from 'react-icons/fa'

const Home = () => {
  const handleNavScroll = (event) => {
    const targetId = event.currentTarget.getAttribute('href')

    if (!targetId || !targetId.startsWith('#')) {
      return
    }

    const targetSection = document.querySelector(targetId)

    if (!targetSection) {
      return
    }

    event.preventDefault()
    targetSection.scrollIntoView({ behavior: 'smooth', block: 'start' })
    window.history.replaceState(null, '', targetId)
  }

  useEffect(() => {
    const animatedElements = Array.from(document.querySelectorAll('.reveal'))

    if (!animatedElements.length) {
      return undefined
    }

    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches

    if (reducedMotion) {
      animatedElements.forEach((element) => element.classList.add('in-view'))
      return undefined
    }

    const observer = new IntersectionObserver(
      (entries) => {
        entries.forEach((entry) => {
          if (entry.isIntersecting) {
            entry.target.classList.add('in-view')
            observer.unobserve(entry.target)
          }
        })
      },
      {
        threshold: 0.16,
        rootMargin: '0px 0px -8% 0px',
      }
    )

    animatedElements.forEach((element) => observer.observe(element))

    return () => observer.disconnect()
  }, [])

  return (
    <div className='portfolio-page'>
      <SpaceScroll />

      <div className='sticky-nav reveal reveal-up' style={{ '--reveal-delay': '50ms' }}>
        <nav className='top-nav'>
          <a href='#about' className='brand' onClick={handleNavScroll}>HAQI.SPACE</a>
          <div className='nav-links'>
            <a href='#about' onClick={handleNavScroll}>HOME</a>
            <a href='#about' onClick={handleNavScroll}>ABOUT</a>
            <a href='#projects' onClick={handleNavScroll}>PROJECTS</a>
            <a href='#contact' onClick={handleNavScroll}>CONTACT</a>
          </div>
          <div className='nav-stars' aria-hidden='true'>✦ ✧</div>
        </nav>
      </div>

      <section className='content-section reveal reveal-up' id='about'>
        <div className='about-grid'>
          <div className='reveal reveal-left'>
            <p className='section-index reveal reveal-up' style={{ '--reveal-delay': '60ms' }}>01 / ARCHITECTURE VOID</p>
            <h2 className='reveal reveal-up' style={{ '--reveal-delay': '120ms' }}>
              Bangkit Haqi Aliaffuan
              <br />
              <span>Software Engineer</span>
            </h2>
            <p className='reveal reveal-up' style={{ '--reveal-delay': '180ms' }}>
              I am a technology enthusiast who always stays up-to-date with the latest developments and is
              ready to quickly adapt to emerging innovations. I have a passion for using logic to solve
              complex problems, while staying open to long-term opportunities that create real impact.
            </p>
            <p className='reveal reveal-up' style={{ '--reveal-delay': '220ms' }}>
              I completed Full Course Learning Coding at IT Brain Software House, served as Project Manager in
              DIGIFORWARD 2025, and currently work as a Freelance Software Developer with AI-assisted workflow
              and practical delivery for web and mobile solutions.
            </p>
            <div className='stat-row'>
              <div className='reveal reveal-up' style={{ '--reveal-delay': '260ms' }}>
                <strong>10+</strong>
                <span>Apps Delivered</span>
              </div>
              <div className='reveal reveal-up' style={{ '--reveal-delay': '320ms' }}>
                <strong>2</strong>
                <span>Competition Awards</span>
              </div>
              <div className='reveal reveal-up' style={{ '--reveal-delay': '380ms' }}>
                <strong>2024</strong>
                <span>Freelance Start</span>
              </div>
            </div>
          </div>

          <div className='about-panel reveal reveal-right' style={{ '--reveal-delay': '200ms' }}>
            <div className='about-panel__badge'>EDUCATION: SMK NEGERI 2 BUDURAN</div>
            <div className='about-panel__core' />
          </div>
        </div>

        <div className='skill-constellation reveal reveal-up'>
          <p className='section-index skill-index reveal reveal-up' style={{ '--reveal-delay': '40ms' }}>02 / CORE CAPABILITIES</p>
          <h3 className='reveal reveal-up' style={{ '--reveal-delay': '120ms' }}>Skill Constellations</h3>

          <div className='solar-skill-system reveal reveal-zoom' style={{ '--reveal-delay': '180ms' }}>
            <div className='orbit-ring orbit-ring--inner' aria-hidden='true' />
            <div className='orbit-ring orbit-ring--outer' aria-hidden='true' />

            <div className='solar-core'>
              <FaCogs className='solar-core__icon' />
              <span>SYSTEMS</span>
              <strong>CORE</strong>
            </div>

            {/* Inner orbit — radius 230px */}
            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(330deg)', '--radius': '230px', '--duration': '26s', '--delay': '-2s' }}
            >
              <article className='orbit-skill-card'>
                <FaCode className='skill-card__icon-svg' />
                <small>Frontend</small>
                <strong>Fullstack Web Development</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(30deg)', '--radius': '230px', '--duration': '26s', '--delay': '-7s' }}
            >
              <article className='orbit-skill-card'>
                <FaPaintBrush className='skill-card__icon-svg' />
                <small>Design</small>
                <strong>UI/UX Design</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(150deg)', '--radius': '230px', '--duration': '26s', '--delay': '-11s' }}
            >
              <article className='orbit-skill-card'>
                <FaCogs className='skill-card__icon-svg' />
                <small>Operations</small>
                <strong>DevOps Basic Skill</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--hard'
              style={{ transform: 'rotate(210deg)', '--radius': '230px', '--duration': '26s', '--delay': '-16s' }}
            >
              <article className='orbit-skill-card'>
                <FaTools className='skill-card__icon-svg' />
                <small>Tools</small>
                <strong>Git / VS Code / Postman</strong>
              </article>
            </div>

            {/* Outer orbit — radius 380px */}
            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(0deg)', '--radius': '380px', '--duration': '38s', '--delay': '-5s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaComments className='skill-card__icon-svg' />
                <small>Synergy</small>
                <strong>Communication</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(90deg)', '--radius': '380px', '--duration': '38s', '--delay': '-10s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaUsers className='skill-card__icon-svg' />
                <small>Navigation</small>
                <strong>Leadership</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(180deg)', '--radius': '380px', '--duration': '38s', '--delay': '-20s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaPuzzlePiece className='skill-card__icon-svg' />
                <small>Resolution</small>
                <strong>Problem Solving</strong>
              </article>
            </div>

            <div
              className='orbit-node orbit-node--soft'
              style={{ transform: 'rotate(270deg)', '--radius': '380px', '--duration': '38s', '--delay': '-27s' }}
            >
              <article className='orbit-skill-card orbit-skill-card--soft'>
                <FaSyncAlt className='skill-card__icon-svg' />
                <small>Expansion</small>
                <strong>Adaptability</strong>
              </article>
            </div>
          </div>

          <div className='skill-legend reveal reveal-up' style={{ '--reveal-delay': '300ms' }}>
            <span><i className='legend-dot legend-dot--hard' /> Technical Hard Skills</span>
            <span><i className='legend-dot legend-dot--soft' /> Synergistic Soft Skills</span>
          </div>
        </div>
      </section>

      <section className='content-section reveal reveal-up' id='projects'>
        <div className='section-header reveal reveal-up' style={{ '--reveal-delay': '70ms' }}>
          <p className='section-index'>03 / PROJECTS</p>
          <h2>Selected Works</h2>
        </div>
        <div className='project-grid'>
          <article className='project-card reveal reveal-up' style={{ '--reveal-delay': '160ms' }}>
            <div className='project-thumb project-thumb--one' />
            <h3>Crypto Price Tracking Website</h3>
            <p>Website pelacakan harga cryptocurrency real-time dengan pencarian coin, watchlist, dan detail aset yang dipersonalisasi.</p>
            <small>WEB APP • REAL-TIME DATA</small>
          </article>
          <article className='project-card reveal reveal-up' style={{ '--reveal-delay': '240ms' }}>
            <div className='project-thumb project-thumb--two' />
            <h3>TrashCare: Smart Waste Pickup & Reuse Marketplace</h3>
            <p>Aplikasi mobile untuk pickup sampah gratis dan marketplace barang bekas guna mendorong circular use di komunitas.</p>
            <small>MOBILE APP • SUSTAINABILITY</small>
          </article>
          <article className='project-card reveal reveal-up' style={{ '--reveal-delay': '320ms' }}>
            <div className='project-thumb project-thumb--three' />
            <h3>Class Monitoring Application for School Operations</h3>
            <p>Aplikasi monitoring kelas untuk attendance, jadwal, tugas, dan progress siswa agar operasional sekolah lebih rapi dan efisien.</p>
            <small>EDTECH • OPERATIONS</small>
          </article>
        </div>
      </section>

      <section className='content-section contact reveal reveal-up' id='contact'>
        <div className='contact-grid'>
          <div className='reveal reveal-left' style={{ '--reveal-delay': '120ms' }}>
            <p className='section-index'>04 / CONTACT</p>
            <h2>Let&apos;s Connect.</h2>
            <p>
              Open for collaboration, project opportunities, and software development partnerships.
            </p>
            <ul className='contact-meta'>
              <li>Phone: 0822-2935-4434</li>
              <li>Email: bangkithaqialiafuan@gmail.com</li>
              <li>Address: Taman Apsari, MCA Blok P1-39, Sidoarjo 61272</li>
            </ul>
          </div>

          <form className='contact-form reveal reveal-right' style={{ '--reveal-delay': '180ms' }} onSubmit={(event) => event.preventDefault()}>
            <label htmlFor='identity'>Name</label>
            <input id='identity' placeholder='Your name' />

            <label htmlFor='signal'>Email</label>
            <input id='signal' placeholder='your@email.com' />

            <label htmlFor='message'>Message</label>
            <textarea id='message' rows='4' placeholder='Write your message...' />

            <button type='submit'>SEND MESSAGE</button>
          </form>
        </div>
      </section>

      <footer className='site-footer reveal reveal-up' style={{ '--reveal-delay': '120ms' }}>
        <div className='footer-grid'>
          <div className='footer-brand'>
            <a href='#about' className='brand' onClick={handleNavScroll}>HAQI.SPACE</a>
            <p>
              Portfolio of Bangkit Haqi Aliaffuan — software engineer focused on practical,
              impactful digital products.
            </p>
          </div>

          <div className='footer-col'>
            <h4>Navigate</h4>
            <a href='#about' onClick={handleNavScroll}>About</a>
            <a href='#projects' onClick={handleNavScroll}>Projects</a>
            <a href='#contact' onClick={handleNavScroll}>Contact</a>
          </div>

          <div className='footer-col'>
            <h4>Contact</h4>
            <p>0822-2935-4434</p>
            <p>bangkithaqialiafuan@gmail.com</p>
            <p>Sidoarjo, Indonesia</p>
          </div>

          <div className='footer-col'>
            <h4>Availability</h4>
            <p>Open for freelance and collaboration projects.</p>
            <a href='#contact' className='footer-cta' onClick={handleNavScroll}>Start a Project</a>
          </div>
        </div>

        <div className='footer-bottom'>
          <span>© 2026 Bangkit Haqi Aliaffuan</span>
          <span>Built with React + GSAP</span>
        </div>
      </footer>
    </div>
  )
}

export default Home
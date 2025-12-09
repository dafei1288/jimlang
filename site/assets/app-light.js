// Theme manager (light default): load saved theme or fall back to light; toggle on click; keep header padding
(function(){
  var key = 'theme';
  var html = document.documentElement;
  var saved = null;
  try { saved = localStorage.getItem(key); } catch(e){}
  var initial = (saved === 'light' || saved === 'dark') ? saved : 'light';
  html.setAttribute('data-theme', initial);
  var btn = document.getElementById('themeToggle');
  if (btn) {
    btn.style.display = '';
    btn.disabled = false;
    btn.addEventListener('click', function(){
      var current = html.getAttribute('data-theme') || 'light';
      var next = current === 'dark' ? 'light' : 'dark';
      html.setAttribute('data-theme', next);
      try { localStorage.setItem(key, next); } catch(e){}
    });
  }
  var header = document.querySelector('.site-header');
  function apply(){ if(header){ document.documentElement.style.setProperty('--header-h', header.offsetHeight+'px'); } }
  if (document.readyState === 'complete' || document.readyState === 'interactive') apply();
  window.addEventListener('load', apply);
  window.addEventListener('resize', apply);
})();

// Nav active state + header opacity on scroll (same as dark)
(function(){
  var header = document.querySelector('.site-header');
  var links = Array.prototype.slice.call(document.querySelectorAll('.nav a[href^="#"]'));
  var sections = links.map(function(a){ try { return document.querySelector(a.getAttribute('href')); } catch(e){ return null; } });
  function setActive(idx){ links.forEach(function(a,i){ a.classList.toggle('active', i===idx); }); }

  if ('IntersectionObserver' in window){
    var io = new IntersectionObserver(function(entries){
      entries.forEach(function(entry){
        var idx = sections.indexOf(entry.target);
        if (entry.isIntersecting) setActive(idx);
      });
    }, { rootMargin: '-40% 0px -50% 0px', threshold: 0.01 });
    sections.forEach(function(s){ if (s) io.observe(s); });
  }

  function onScroll(){ if (header) header.classList.toggle('scrolled', window.scrollY > 10); }
  onScroll();
  window.addEventListener('scroll', onScroll, { passive:true });
  links.forEach(function(a,i){ a.addEventListener('click', function(){ setActive(i); }); });
})();

// Add copy buttons to code blocks
(function(){
  if (!navigator.clipboard) return;
  var blocks = document.querySelectorAll('pre > code');
  blocks.forEach(function(code){
    var pre = code.parentElement;
    pre.style.position = 'relative';
    var btn = document.createElement('button');
    btn.className = 'btn copy-btn';
    btn.textContent = 'Copy';
    btn.style.position = 'absolute';
    btn.style.top = '8px';
    btn.style.right = '8px';
    btn.style.padding = '4px 8px';
    btn.style.fontSize = '12px';
    btn.style.borderRadius = '8px';
    btn.style.opacity = '0.8';
    btn.addEventListener('click', function(){
      var text = code.textContent.replace(/\r\n/g, '\n');
      navigator.clipboard.writeText(text).then(function(){
        btn.textContent = 'Copied'; setTimeout(function(){ btn.textContent = 'Copy'; }, 1200);
      }).catch(function(){});
    });
    pre.appendChild(btn);
  });
})();